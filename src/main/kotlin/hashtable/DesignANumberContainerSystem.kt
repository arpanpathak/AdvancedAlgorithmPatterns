package hashtable

import java.util.*

class DesignANumberContainerSystem {
    private val numberToIndices: MutableMap<Int, SortedSet<Int>> = mutableMapOf()
    private val indexToNumber: MutableMap<Int, Int> = mutableMapOf()

    // Change the number at the given index
    fun change(index: Int, number: Int) {
        // If the index was previously associated with a number, remove it from that number's set
        indexToNumber[index]?.let { previousNumber ->
            numberToIndices[previousNumber]?.remove(index)
        }

        // Update the index to the new number
        indexToNumber[index] = number

        // Add the index to the new number's set
        numberToIndices.computeIfAbsent(number) { TreeSet() }.add(index)
    }

    // Find the smallest index where the given number is stored
    fun find(number: Int): Int {
        val indices = numberToIndices[number]
        // If no index contains the number, return -1
        return indices?.firstOrNull() ?: -1
    }
}