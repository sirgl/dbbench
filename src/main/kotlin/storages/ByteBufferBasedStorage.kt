package storages

import algo.ByteBufferListAccessor
import algo.IntArrayListAccessor
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import algo.syncSortedBypass
import java.nio.ByteBuffer

interface ByteBufferBasedStorage : Storage {
    fun iterateInternal(itemHandler: (key: ByteBuffer, value: ByteBuffer) -> Unit)
}

fun ByteBufferBasedStorage.searchKeysWhichValuesContainsAnyOfImpl(target: IntArrayList): IntSet {
    val keys = IntOpenHashSet()
    iterateInternal { key, value ->
        val targetAccessor = IntArrayListAccessor(target)
        val valueAccessor = ByteBufferListAccessor(value)
        var valueContainsAnyTargetValue = false
        syncSortedBypass(targetAccessor, valueAccessor, onFirstEl = { true }, onSecondEl = { true }, onBothEl = {
            valueContainsAnyTargetValue = true
            false
        })
        if (valueContainsAnyTargetValue) {
            keys.add(key.int)
        }
    }
    return keys
}