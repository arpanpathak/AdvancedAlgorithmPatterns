package hashtable

class FileSystem() {
    private val fileSystem = mutableMapOf<String, Int>()

    fun createPath(path: String, value: Int): Boolean {

        if (path in fileSystem || path == "/") {
            return false
        }

        val parentPath = path.substringBeforeLast("/")
        if (parentPath.isNotEmpty() && parentPath !in fileSystem) {
            return false
        }

        fileSystem[path] = value
        return true
    }

    fun get(path: String): Int {
        return fileSystem[path] ?: -1
    }
}
