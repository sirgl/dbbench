package storages

import com.github.pbbl.direct.DirectByteBufferPool
import algo.deserializeList
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntSet
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import algo.putInBuffer
import algo.withKeyValue
import java.nio.ByteBuffer
import java.nio.file.Path

class LmdbStorage(path: Path) : AutoCloseable, ByteBufferBasedStorage {
    private val env: Env<ByteBuffer> = Env.create()
        .setMapSize(1000 * 1024 * 1024)
        .setMaxDbs(1)
        .open(path.toFile())

    private val pool = DirectByteBufferPool()

    private val dbi = env.openDbi("db", DbiFlags.MDB_CREATE)

    override fun put(key: Int, value: IntArrayList) {
        withKeyValue(pool, key, value) { keyBuffer, valueBuffer ->
            env.txnWrite().use { txn ->
                dbi.put(txn, keyBuffer, valueBuffer)
                txn.commit()
            }
        }
    }

    override fun get(key: Int) : IntArrayList? {
        val keyBuffer = putInBuffer(pool, Int.SIZE_BYTES) {
            putInt(key)
        }
        val list = env.txnRead().use { txn ->
            val value = dbi.get(txn, keyBuffer) ?: return@use null
            deserializeList(value)
        }
        pool.give(keyBuffer)
        return list
    }

    // values are sorted
    override fun searchKeysWhichValuesContainsAnyOf(target: IntArrayList): IntSet {
        return this.searchKeysWhichValuesContainsAnyOfImpl(target)
    }

    override fun iterate(itemHandler: (key: Int, value: IntArrayList) -> Unit) {
        iterateInternal { key: ByteBuffer, value: ByteBuffer ->
            itemHandler(key.int, deserializeList(value))
        }
    }


    override fun iterateInternal(itemHandler: (key: ByteBuffer, value: ByteBuffer) -> Unit) {
        env.txnRead().use { txn ->
            dbi.openCursor(txn).use { cursor ->
                if (!cursor.first()) return
                while (true) {
                    itemHandler(cursor.key(), cursor.`val`())
                    if (!cursor.next()) return
                }
            }
        }
    }

    override fun close() {
        pool.close()
        dbi.close()
        env.close()
    }
}