package string

class MaximumValueOfAStringIsAnArray {
    fun maximumValue(strs: Array<String>): Int {
        return strs.maxOf {
            val numericValue = it.toIntOrNull()
            numericValue?: it.length
        }
    }
}
