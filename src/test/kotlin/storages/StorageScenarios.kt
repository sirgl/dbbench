package storages

import algo.iList
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import kotlin.test.assertEquals

object StorageScenarios {
    fun testPutGet(storage: Storage) {
        val initial = iList(5, 99, 823)
        storage.put(10, initial)
        val stored = storage.get(10)
        assertEquals(initial, stored)
    }

    fun testIterate(storage: Storage) {
        val l1 = iList(5, 99, 823)
        val l2 = iList(1, 23, 44, 55, 99)
        storage.put(10, l1)
        storage.put(20, l2)
        val map = HashMap<Int, IntArrayList>()
        storage.iterate { key, value ->
            map[key] = value
        }
        assertEquals(hashMapOf(10 to l1, 20 to l2), map)
    }

    fun testSearch(storage: Storage) {
        val l1 = iList(5, 99, 823)
        val l2 = iList(1, 23, 44, 55, 99)
        val l3 = iList(-100)
        val l4 = iList()
        storage.put(1, l1)
        storage.put(2, l2)
        storage.put(3, l3)
        storage.put(4, l4)
        val keys = storage.searchKeysWhichValuesContainsAnyOf(iList(-100, 55))
        assertEquals(IntOpenHashSet.of(2, 3), keys)
    }
}