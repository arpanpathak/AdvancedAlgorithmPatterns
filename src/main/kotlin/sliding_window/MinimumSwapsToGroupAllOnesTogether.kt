package sliding_window

class MinimumSwapsToGroupAllOnesTogether {
    fun minSwaps(data: IntArray): Int {
        val totalOnes = data.count { it == 1 }

        if (totalOnes == 0 || totalOnes == data.size) {
            return 0  // Already grouped or no 1's
        }

        var maxOnesInWindow = 0
        var currentOnesInWindow = 0

        // Initial window of size `totalOnes`
        for (i in 0 until totalOnes) {
            if (data[i] == 1) currentOnesInWindow++
        }

        maxOnesInWindow = currentOnesInWindow

        // Slide the window through the rest of the array
        for (i in totalOnes until data.size) {
            if (data[i] == 1) currentOnesInWindow++
            if (data[i - totalOnes] == 1) currentOnesInWindow--

            maxOnesInWindow = maxOf(maxOnesInWindow, currentOnesInWindow)
        }

        return totalOnes - maxOnesInWindow
    }
}