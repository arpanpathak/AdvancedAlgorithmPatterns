package stack

class SumOfSubArrayMinimum {
    fun sumSubarrayMins(arr: IntArray): Int {
        val n = arr.size
        val stack = ArrayDeque<Int>()
        var sum = 0L // Use Long to handle overflow
        val mod = 1_000_000_007

        for (i in 0..n) {
            val curr = if (i < n) arr[i] else Int.MIN_VALUE // Use a sentinel value to flush the stack

            while (stack.isNotEmpty() && arr[stack.last()] > curr) {
                val mid = stack.removeLast()
                val left = if (stack.isEmpty()) -1 else stack.last()
                val right = i

                // Calculate count of subarrays where arr[mid] is the minimum
                val count = (mid - left).toLong() * (right - mid).toLong()

                // Add the contribution of arr[mid]
                sum = (sum + arr[mid].toLong() * count) % mod
            }
            stack.addLast(i)
        }

        return sum.toInt()
    }
}
