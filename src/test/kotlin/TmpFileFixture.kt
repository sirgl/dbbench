import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes


class TmpFileFixture : AutoCloseable {
    private val tmpDirs: MutableList<Path> = ArrayList()

    fun getTmpDir(prefix: String): Path {
        val directory = Files.createTempDirectory(prefix)
        tmpDirs.add(directory)
        return directory
    }

    private fun clear() {
        for (tmpDir in tmpDirs) {
            deleteRecursively(tmpDir)
        }
        tmpDirs.clear()
    }

    private fun deleteRecursively(pathToBeDeleted: Path) {
        Files.walkFileTree(pathToBeDeleted,
            object : SimpleFileVisitor<Path>() {
                @Throws(IOException::class)
                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                    Files.delete(dir)
                    return FileVisitResult.CONTINUE
                }

                @Throws(IOException::class)
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    Files.delete(file)
                    return FileVisitResult.CONTINUE
                }
            })
    }

    override fun close() {
        clear()
    }
}