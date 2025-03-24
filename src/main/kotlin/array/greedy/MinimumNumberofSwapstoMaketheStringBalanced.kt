package array.greedy

class MinimumNumberofSwapstoMaketheStringBalanced {
    fun minSwaps(s: String): Int {
        var imbalance = 0
        var maxImbalance = 0

        for (char in s) {
            if (char == '[') {
                imbalance--
            } else { // char == ']'
                imbalance++
            }
            maxImbalance = maxOf(maxImbalance, imbalance)
        }

        return (maxImbalance + 1) / 2
    }
}