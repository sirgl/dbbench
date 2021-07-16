package storages

import kotlin.test.Test

class InMemoryStorageTest {
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
        val storage = InMemoryStorage()
        test(storage)
    }
}