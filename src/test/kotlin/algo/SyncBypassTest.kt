package algo

import algo.IntArrayListAccessor
import algo.iList
import algo.syncSortedBypass
import it.unimi.dsi.fastutil.ints.IntArrayList
import kotlin.test.Test
import kotlin.test.assertEquals

class SyncBypassTest {
    @Test
    fun testBypass1() {
        doTest(
            l1 = iList(1, 3, 6, 10),
            l2 = iList(1, 4, 10, 22),
            only1 = iList(3, 6),
            only2 = iList(4, 22),
            both = iList(1, 10)
        )
    }

    @Test
    fun testBypass2() {
        doTest(
            l1 = iList(1, 3, 6, 10),
            l2 = iList(),
            only1 = iList(1, 3, 6, 10),
            only2 = iList(),
            both = iList()
        )
    }

    @Test
    fun testBypass3() {
        doTest(
            l1 = iList(1, 3, 6, 10),
            l2 = iList(3, 5),
            only1 = iList(1, 6, 10),
            only2 = iList(5),
            both = iList(3)
        )
    }

    private fun doTest(
        l1: IntArrayList,
        l2: IntArrayList,
        only1: IntArrayList,
        only2: IntArrayList,
        both: IntArrayList
    ) {
        val only1Actual = IntArrayList()
        val only2Actual = IntArrayList()
        val bothActual = IntArrayList()
        syncSortedBypass(
            IntArrayListAccessor(l1),
            IntArrayListAccessor(l2),
            onFirstEl = {
                only1Actual.add(it)
                true
            },
            onSecondEl = {
                only2Actual.add(it)
                true
            },
            onBothEl = {
                bothActual.add(it)
                true
            })
        assertEquals(only1, only1Actual)
        assertEquals(only2, only2Actual)
        assertEquals(both, bothActual)
    }
}