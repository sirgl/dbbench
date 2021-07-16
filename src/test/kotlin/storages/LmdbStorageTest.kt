package storages

import TmpFileFixture
import kotlin.test.Test

class LmdbStorageTest {
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
            LmdbStorage(fixture.getTmpDir("lmdb")).use { storage ->
                test(storage)
            }
        }
    }
}