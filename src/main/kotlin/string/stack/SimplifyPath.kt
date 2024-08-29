package string.stack

class SimplifyPath {
    fun simplifyPath(path: String): String {
        // Split the path by "/"
        val tokens = path.split("/")

        // Initialize a mutable list to store valid directory names
        val stack = mutableListOf<String>()

        for (token in tokens) {
            when (token) {
                "", "." -> {
                    // Ignore empty or current directory tokens
                    continue
                }
                ".." -> {
                    // Pop from the list if it's not empty (go back to the parent directory)
                    if (stack.isNotEmpty()) {
                        stack.removeLast()
                    }
                }
                else -> {
                    // Push valid directory names onto the list
                    stack.add(token)
                }
            }
        }

        // Rebuild the simplified path from the list
        return "/" + stack.joinToString("/")
    }
}