package array.greedy

class MaximumSwap {
    // Extension Function
    private fun CharArray.swap(i: Int, j: Int) {
        if(i == j) return
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
    }

    fun maximumSwap(num: Int): Int {
        var digits = num.toString().toCharArray()
        var maxIndex = digits.lastIndex
        var swap1 = 0
        var swap2 = 0

        for(i in digits.lastIndex - 1 downTo 0) {
            when {
                digits[maxIndex] == digits[i] -> continue
                digits[maxIndex] < digits[i]  -> maxIndex = i
                else -> {
                    swap1 = maxIndex
                    swap2 = i
                }
            }
        }

        digits.swap(swap1, swap2)

        return String(digits).toInt()
    }
}