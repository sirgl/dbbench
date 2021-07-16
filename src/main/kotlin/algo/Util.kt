package algo

import com.github.pbbl.direct.DirectByteBufferPool
import it.unimi.dsi.fastutil.ints.IntArrayList
import java.nio.ByteBuffer

fun iList(vararg l: Int): IntArrayList = IntArrayList.of(*l)

fun deserializeList(value: ByteBuffer): IntArrayList {
    val size = value.int
    val list = IntArrayList(size)
    repeat(size) {
        list.add(value.int)
    }
    return list
}

inline fun withKeyValue(pool: DirectByteBufferPool, key: Int, value: IntArrayList, b: (key: ByteBuffer, value: ByteBuffer) -> Unit) {
    val keyBuffer = putInBuffer(pool, Int.SIZE_BYTES) {
        putInt(key)
    }
    val valueBuffer = putInBuffer(pool, Int.SIZE_BYTES + Int.SIZE_BYTES * value.size) {
        putInt(value.size)
        for (i in 0 until value.size) {
            putInt(value.getInt(i))
        }
    }
    b(keyBuffer, valueBuffer)
    pool.give(keyBuffer)
    pool.give(valueBuffer)
}

inline fun putInBuffer(pool: DirectByteBufferPool, size: Int, b: ByteBuffer.() -> Unit): ByteBuffer {
    val buffer = pool.take(size)
    b(buffer)
    buffer.flip()
    return buffer
}