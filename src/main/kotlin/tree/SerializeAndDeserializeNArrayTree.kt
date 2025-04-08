package tree

class SerializeAndDeserializeNArrayTree {
    class Node(var `val`: Int) {
        var children: List<Node?> = listOf()
    }

    class Codec {
        // Encodes a tree to a single string.
        fun serialize(root: Node?): String = when (root) {
            null -> ""
            else -> buildString {
                fun dfs(node: Node?) {
                    node?.let {
                        append("${it.`val`}:${it.children.size}")
                        if (it.children.isNotEmpty()) append(",")
                        it.children.forEachIndexed { index, child ->
                            dfs(child)
                            if (index < it.children.size - 1) append(",")
                        }
                    }
                }
                dfs(root)
            }
        }

        // Deserialize with internal DFS
        fun deserialize(data: String): Node? {
            if (data.isEmpty()) return null

            val tokens = data.split(",")
            var index = 0

            fun parse(): Node? {
                if (index >= tokens.size) return null

                // Use destructuring declaration
                val (valueStr, childCountStr) = tokens[index++].split(":")
                val node = Node(valueStr.toInt())

                // Create children using functional approach
                node.children = List(childCountStr.toInt()) { parse() }

                return node
            }

            return parse()
        }
    }
}
