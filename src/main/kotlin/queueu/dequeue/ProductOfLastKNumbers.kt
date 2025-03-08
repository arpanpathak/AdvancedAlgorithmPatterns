package queueu.dequeue

class ProductOfLastKNumbers {
    private val numbers = mutableListOf<Int>()
    private val prefixProducts = mutableListOf(1)

    fun add(num: Int) {
        numbers.add(num)
        // Reset the prefix product list if the current number is 0
        if (num == 0) {
            prefixProducts.clear()
            prefixProducts.add(1)
        } else {

            prefixProducts.add(prefixProducts.last() * num)
        }
    }

    fun getProduct(k: Int): Int {
        val n = prefixProducts.size - 1  // The size of the prefixProducts list is n + 1
        if (k > n) return 0

        return prefixProducts[n] / prefixProducts[n - k]
    }
}