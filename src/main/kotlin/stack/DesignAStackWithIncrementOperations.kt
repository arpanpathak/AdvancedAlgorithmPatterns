package stack

class DesignAStackWithIncrementOperations(maxSize: Int) {
        var maxSize = maxSize
        var stack = ArrayDeque<Int>()
        var increments = IntArray(maxSize)

        fun push(x: Int) {
            if (stack.size < maxSize)
                stack.addLast(x)
        }

        fun pop(): Int {
            if (stack.isEmpty()) return -1

            val index = stack.size - 1
            val value = stack.removeLast() + increments[index]

            if (index > 0) {
                increments[index - 1] += increments[index]  // Carry increment to the next element
            }
            increments[index] = 0  // Reset increment after applying

            return value
        }

        fun increment(k: Int, `val`: Int) {
            val limit = minOf(k, stack.size) - 1
            if (limit >= 0) {
                increments[limit] += `val`
            }
        }

}
