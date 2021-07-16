package storages

import TmpFileFixture
import kotlin.test.Test

class RocksDbTest {
    @Test
    fun testPutGet() {
        scenario(StorageScenarios::testPutGet)
    }

    @Test
    fun testIterate() {
        scenario(StorageScenarios::testIterate)
    }

    @Test
    fun testSearch() {
        scenario(StorageScenarios::testSearch)
    }

    private fun scenario(test: (Storage) -> Unit) {
        TmpFileFixture().use { fixture ->
            RocksDbStorage(fixture.getTmpDir("rocksDB")).use { storage ->
                test(storage)
            }
        }
    }
}