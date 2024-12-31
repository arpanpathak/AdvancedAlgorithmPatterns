package disjointset

class UnionFind<T> {
    data class Node<T>(var parent: T, var rank: Int)

    private val nodes = mutableMapOf<T, Node<T>>()

    fun add(x: T) {
        nodes.putIfAbsent(x, Node(x, 0))
    }

    fun find(x: T): T {
        val node = nodes[x] ?: throw IllegalAccessException("Value $x not found")

        if (node.parent != x)
            node.parent = find(node.parent)

        return node.parent
    }

    fun union(x: T, y: T) {
        val rootX = find(x)
        val rootY = find(y)
        if (rootX != rootY) {
            val nodeX = nodes[rootX]!!
            val nodeY = nodes[rootY]!!
            when {
                nodeX.rank > nodeY.rank -> nodeY.parent = rootX
                nodeX.rank < nodeY.rank -> nodeX.parent = rootY
                else -> {
                    nodeY.parent = rootX
                    nodeX.rank++
                }
            }
        }
    }
}
