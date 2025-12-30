// package array.Combinatorics

class Solution {
    fun <T: Comparable<T>> MutableList<T>.swap(i: Int, j: Int) {
        this[i] = this[j].also { this[j] = this[i] }
    }

    fun <T : Comparable<T>> MutableList<T>.permuteToNextOrElseFalse(): Boolean {
        val pivotIndex = (size - 2 downTo 0).firstOrNull { this[it] < this[it + 1] } ?: run {
            this.reverse()
            return false
        }

        val swapIndex = (size - 1 downTo pivotIndex + 1).first { this[pivotIndex] < this[it] }

        this[pivotIndex] = this[swapIndex].also { this[swapIndex] = this[pivotIndex] }

        this.subList(pivotIndex + 1, size).reverse()

        return true
    }

    fun permuteUnique(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val current = nums.toMutableList().also { it.sort() }

        do {
            result.add(current.toList())
        } while (current.permuteToNextOrElseFalse())

        return result
    }
}
