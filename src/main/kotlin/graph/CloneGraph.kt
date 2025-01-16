package graph

data class Node(val `val`: Int, val neighbors: MutableList<Node?> = mutableListOf())

class CloneGraph {
    val map = mutableMapOf<Node, Node>()

    fun cloneGraph(node: Node?): Node? {

        if (node == null) return null
        map[node]?.let { return it }

        val clonedNode = Node(node?.`val`!!)
        map[node] = clonedNode

        node.neighbors.forEach{ clonedNode.neighbors.add(cloneGraph(it)) }

        return clonedNode
    }
}