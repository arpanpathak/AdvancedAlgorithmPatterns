package stack

class MinStackShort() {
    private data class Node(val value: Int, val currentMin: Int)
    private val stack = ArrayDeque<Node>()

    fun push(value: Int) {
        val min = stack.lastOrNull()?.let { minOf(it.currentMin, value) } ?: value
        stack.addLast(Node(value, min))
    }

    fun pop() {
        stack.takeIf { it.isNotEmpty() }?.removeLast()
    }

    fun top(): Int = stack.last().value

    fun getMin(): Int = stack.last().currentMin
}