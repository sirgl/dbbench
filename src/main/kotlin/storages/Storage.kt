package storages

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntSet

interface Storage {
    fun put(key: Int, value: IntArrayList)

    fun get(key: Int) : IntArrayList?

    // values are sorted
    fun searchKeysWhichValuesContainsAnyOf(target: IntArrayList) : IntSet

    fun iterate(itemHandler: (key: Int, value: IntArrayList) -> Unit)
}