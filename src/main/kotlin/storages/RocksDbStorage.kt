package storages

import com.github.pbbl.direct.DirectByteBufferPool
import algo.deserializeList
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntSet
import org.rocksdb.Options
import org.rocksdb.ReadOptions
import org.rocksdb.RocksDB
import org.rocksdb.WriteOptions
import algo.putInBuffer
import algo.withKeyValue
import java.nio.ByteBuffer
import java.nio.file.Path


class RocksDbStorage(dirPath: Path) : ByteBufferBasedStorage, AutoCloseable {
    companion object {
        init {
            RocksDB.loadLibrary()
        }

        private const val DEFAULT_VALUE_SIZE = 16 * 1024
    }

    private val options = Options().setCreateIfMissing(true)
    private val db = RocksDB.open(options, dirPath.resolve("db").toString())
    private val pool = DirectByteBufferPool()
    private val readOptions = ReadOptions()
    private val writeOptions = WriteOptions()


    override fun put(key: Int, value: IntArrayList) {
        withKeyValue(pool, key, value) { keyBuffer, valueBuffer ->
            db.put(writeOptions, keyBuffer, valueBuffer)
        }
    }


    override fun get(key: Int): IntArrayList? {
        val keyBuffer = putInBuffer(pool, Int.SIZE_BYTES) {
            putInt(key)
        }
        var valueBuffer = pool.take(DEFAULT_VALUE_SIZE)
        val size = db.get(readOptions, keyBuffer, valueBuffer)
        if (size == RocksDB.NOT_FOUND) return null
        if (size > DEFAULT_VALUE_SIZE) {
            pool.give(valueBuffer)
            valueBuffer = pool.take(size)
            db.get(readOptions, keyBuffer, valueBuffer) // TODO ensure size is proper
        }
        val list = deserializeList(valueBuffer)
        pool.give(keyBuffer)
        pool.give(valueBuffer)
        return list
    }

    override fun searchKeysWhichValuesContainsAnyOf(target: IntArrayList): IntSet {
        return this.searchKeysWhichValuesContainsAnyOfImpl(target)
    }

    override fun iterate(itemHandler: (key: Int, value: IntArrayList) -> Unit) {
        iterateInternal { keyBuffer, valueBuffer ->
            val key = keyBuffer.int
            val value = deserializeList(valueBuffer)
            itemHandler(key, value)
        }
    }

    override fun iterateInternal(itemHandler: (key: ByteBuffer, value: ByteBuffer) -> Unit) {
        db.newIterator().use { iterator ->
            iterator.seekToFirst()
            while (iterator.isValid) {
                val keyBuffer = pool.take(Int.SIZE_BYTES)
                iterator.key(keyBuffer)
                var valueBuffer = pool.take(DEFAULT_VALUE_SIZE)
                val size = iterator.value(valueBuffer)
                if (size > DEFAULT_VALUE_SIZE) {
                    pool.give(valueBuffer)
                    valueBuffer = pool.take(size)
                    iterator.value(valueBuffer) // TODO ensure size is proper
                }

                itemHandler(keyBuffer, valueBuffer)
                pool.give(keyBuffer)
                pool.give(valueBuffer)
                iterator.next()
            }
        }
    }

    override fun close() {
        options.close()
        db.close()
        pool.close()
    }
}