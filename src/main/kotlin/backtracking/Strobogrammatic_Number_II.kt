package backtracking

class Strobogrammatic_Number_II {
    fun findStrobogrammatic(n: Int): List<String> {
        val pairs = listOf("0" to "0", "1" to "1", "6" to "9", "8" to "8", "9" to "6")
        fun generateStrobogrammatic(currentLength: Int): List<String> {
            if (currentLength == 0) return listOf("")
            if (currentLength == 1) return listOf("0", "1", "8")

            val result = mutableListOf<String>()

            for ((left, right) in pairs) {
                // Avoid leading zeros
                if (currentLength == n && left == "0") continue

                // Generate the inner strobogrammatic numbers
                val innerNumbers = generateStrobogrammatic(currentLength - 2)

                // Append the current pair to the inner numbers
                for (inner in innerNumbers) {
                    result.add(left + inner + right)
                }
            }

            return result
        }

        return generateStrobogrammatic(n)
    }
}