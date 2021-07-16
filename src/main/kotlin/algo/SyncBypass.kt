package algo

import it.unimi.dsi.fastutil.ints.IntArrayList
import java.nio.ByteBuffer

// sorted bypass by 2 list at once
inline fun syncSortedBypass(
    first: ListAccessor,
    second: ListAccessor,
    onFirstEl: (Int) -> Boolean,
    onSecondEl: (Int) -> Boolean,
    onBothEl: (Int) -> Boolean
) {
    while (first.isValid() && second.isValid()) {
        val firstElem = first.current()
        val secondElem = second.current()
        when {
            firstElem < secondElem -> {
                if (!onFirstEl(firstElem)) return
                first.advance()
            }
            firstElem > secondElem -> {
                if (!onSecondEl(secondElem)) return
                second.advance()
            }
            else -> {
                if (!onBothEl(firstElem)) return
                first.advance()
                second.advance()
            }
        }
    }
    while (first.isValid()) {
        if (!onFirstEl(first.current())) return
        first.advance()
    }
    while (second.isValid()) {
        if (!onSecondEl(second.current())) return
        second.advance()
    }
}

interface ListAccessor {
    fun current() : Int
    fun advance()
    fun isValid() : Boolean
}

class IntArrayListAccessor(private val list: IntArrayList) : ListAccessor {
    private var index: Int = 0

    override fun current(): Int {
        return list.getInt(index)
    }

    override fun advance() {
        index++
    }

    override fun isValid(): Boolean {
        return index < list.size
    }
}

class ByteBufferListAccessor(private val buffer: ByteBuffer) : ListAccessor {
    private val size = buffer.int
    private var index = 0
    private var cached = if (size == 0) Int.MIN_VALUE else buffer.int

    override fun current(): Int {
        return cached
    }

    override fun advance() {
        index++
        cached = if (isValid()) {
            buffer.int
        } else {
            Int.MIN_VALUE
        }
    }

    override fun isValid(): Boolean {
        return index < size
    }
}