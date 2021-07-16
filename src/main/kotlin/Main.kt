import storages.InMemoryStorage
import storages.LmdbStorage
import storages.RocksDbStorage
import storages.Storage
import java.nio.file.Paths

fun main() {
    val path = Paths.get("/Users/roman.ivanov/IdeaProjects/dbbench/data")
//    RocksDbStorage(path).use { storage ->
//        runBench(storage, true)
//    }
    runBench(InMemoryStorage(), true)
}

private fun runBench(storage: Storage, genData: Boolean) {
    val benchmark = Benchmark(storage, 100 * 1024 * 1024)
    if (genData) {
        benchmark.generateAndPutData()
    }
    while (true) {
        val nsSearch = benchmark.doSearch()
        println(nsSearch / 1000_000)
    }
}

