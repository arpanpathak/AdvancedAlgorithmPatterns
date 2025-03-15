package tree

/**
 * Definition for a binary tree node.
 * class TreeNode(var `val`: Int) {
 *     var left: TreeNode? = null
 *     var right: TreeNode? = null
 * }
 */

class Codec() {
    // Encodes a URL to a shortened URL.
    fun serialize(root: TreeNode?): String {
        val serializedTree = StringBuilder()

        fun dfs(node: TreeNode?) {
            if (node == null) {
                serializedTree.append("null,")
                return
            }

            serializedTree.append("${node.`val`},").also {
                dfs(node.left)
                dfs(node.right)
            }
        }
        dfs(root)
        return serializedTree.toString()
    }

    // Decodes the encoded string to tree using a recursive approach.
    fun deserialize(data: String): TreeNode? {
        val nodes = data.split(",").toMutableList()

        fun dfsDeserialize(): TreeNode? {
            if (nodes.isEmpty()) return null
            val value = nodes.removeAt(0)
            if (value == "null") return null

            val node = TreeNode(value.toInt()).apply {
                left = dfsDeserialize()
                right = dfsDeserialize()
            }

            return node
        }

        return dfsDeserialize()
    }
}

/**
 * Your Codec object will be instantiated and called as such:
 * var ser = Codec()
 * var deser = Codec()
 * var data = ser.serialize(longUrl)
 * var ans = deser.deserialize(data)
 */