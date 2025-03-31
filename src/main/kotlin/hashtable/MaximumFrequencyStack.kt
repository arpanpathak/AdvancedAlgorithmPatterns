package hashtable

class FreqStack() {
    val freq = mutableMapOf<Int, Int>()
    val groups = mutableMapOf<Int, ArrayDeque<Int>>()
    var maxFreq = 0

    fun push(`val`: Int) {
        val count = (freq[`val`]?:0) + 1
        freq[`val`] = count
        if (count > maxFreq)
            maxFreq = count
        if (count !in groups)
            groups[count] = ArrayDeque()
        groups[count]?.add(`val`)

    }

    fun pop(): Int {
        // Pop the element from the stack of the max frequency
        val top = groups[maxFreq]?.removeLast()

        // Decrease the frequency of the popped element
        if (top != null) {
            freq[top] = freq[top]!! - 1
            // If the frequency of the popped element is now less than maxFreq, adjust maxFreq
            if (groups[maxFreq]?.isEmpty() == true) {
                maxFreq--
            }
        }

        return top ?: -1 // Return the popped element (or -1 if no element to pop)
    }

}

