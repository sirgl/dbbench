import algo.DataGenerator
import algo.iList
import storages.Storage
import kotlin.system.measureNanoTime

class Benchmark(
    private val storage: Storage,
    private val valueBytesSize: Int,
    private val seed: Long = 10L,
) {
    private var sum = 0

    fun generateAndPutData() {
        val generator = DataGenerator(seed)
        val data = generator.generateSequence(valueBytesSize / 4) // convert to ints
        val timeNs = measureNanoTime {
            for ((key, value) in data) {
                storage.put(key, value)
            }
        }
        println("Filled DB in ${timeNs / 1_000_000}ms")
    }

    fun doSearch() : Long {
        return measureNanoTime {
            val keys = storage.searchKeysWhichValuesContainsAnyOf(iList(-2079478307, -2033158943, -1851571045, -1017360484))
            sum += keys.size
        }
    }
}