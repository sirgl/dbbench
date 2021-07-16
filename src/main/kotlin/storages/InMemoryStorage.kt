package storages

import algo.IntArrayListAccessor
import it.unimi.dsi.fastutil.ints.*
import algo.syncSortedBypass

class InMemoryStorage : Storage {
    private val storage = Int2ObjectOpenHashMap<IntArrayList>()

    override fun put(key: Int, value: IntArrayList) {
        storage[key] = value
    }

    override fun get(key: Int): IntArrayList? {
        return storage.get(key)
    }

    override fun searchKeysWhichValuesContainsAnyOf(target: IntArrayList): IntSet {
        val keys = IntOpenHashSet()
        storage.int2ObjectEntrySet().fastForEach {
            val value = it.value

            val targetAccessor = IntArrayListAccessor(target)
            val valueAccessor = IntArrayListAccessor(value)
            var valueContainsAnyTargetValue = false
            syncSortedBypass(targetAccessor, valueAccessor, onFirstEl = { true }, onSecondEl = { true }, onBothEl = {
                valueContainsAnyTargetValue = true
                false
            })
            if (valueContainsAnyTargetValue) {
                keys.add(it.intKey)
            }
        }
        return keys
    }

    override fun iterate(itemHandler: (key: Int, value: IntArrayList) -> Unit) {
        storage.int2ObjectEntrySet().fastForEach {
            itemHandler(it.intKey, it.value)
        }
    }
}