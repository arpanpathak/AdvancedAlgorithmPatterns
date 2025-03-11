package stack

import array.dfs.NestedInteger

class NestedIterator(nestedList: List<NestedInteger>) {
    private val stack = ArrayDeque<NestedInteger>()

    init {
        // Initialize the stack with the nested list, reversed so we can pop from the back
        for (item in nestedList.reversed()) {
            stack.addLast(item)
        }
    }

    fun next(): Int {
        // hasNext guarantees that the next item will be an integer
        return stack.removeLast().getInteger()!!
    }

    fun hasNext(): Boolean {
        // Make sure the top of the stack is an integer, if not, pop the nested list
        while (stack.isNotEmpty() && !stack.last().isInteger()) {
            val current = stack.removeLast()
            val nestedList = current.getList()
            // Push the elements of the nested list onto the stack, in reverse order
            for (item in nestedList!!.reversed()) {
                stack.addLast(item)
            }
        }
        // Now check if there's an integer at the top of the stack
        return stack.isNotEmpty() && stack.last().isInteger()
    }
}
