package algo

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntComparators
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import kotlin.random.Random

class DataGenerator(seed: Long) {
    private val random = Random(seed)
    private val keys = IntOpenHashSet()

    fun generateSequence(size: Int) : Sequence<Pair<Int, IntArrayList>> {
        return sequence {
            var totalLength = 0
            while (totalLength < size) {
                val length = random.nextInt(0, 10000)
                totalLength += length
                val list = generateSortedList(length)
                yield(nextKey() to list)
            }
        }
    }

    private fun nextKey(): Int {
        while (true) {
            val candidate = random.nextInt()
            if (keys.add(candidate)) {
                return candidate
            }
        }
    }

    private fun generateSortedList(length: Int) : IntArrayList {
        val list = IntArrayList()
        repeat(length) {
            list.add(random.nextInt())
        }
        list.sort(IntComparators.NATURAL_COMPARATOR)
        return list
    }
}