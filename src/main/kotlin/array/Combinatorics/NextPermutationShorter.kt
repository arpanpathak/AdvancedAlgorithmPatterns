package array.Combinatorics

fun <T : Comparable<T>> MutableList<T>.nextPermutation(): Boolean {
    val pivotIndex = (size - 2 downTo 0).firstOrNull { this[it] < this[it + 1]} ?: run {
        this.reverse()
        return false
    }

    val swapIndex = (size - 1 downTo pivotIndex + 1).first { this[pivotIndex] < this[it] }

    this[pivotIndex] = this[swapIndex].also { this[swapIndex] = this[pivotIndex] }

    this.subList(pivotIndex + 1, size).reverse()

    return true
}