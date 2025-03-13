package array.hashtable

class IntegerToEnglishWords {
    class Solution {
        private val lessThan20 = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        )
        private val tens = arrayOf(
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        )
        private val thousands = arrayOf("", "Thousand", "Million", "Billion")

        fun numberToWords(num: Int): String {
            if (num == 0) return "Zero"

            var n = num
            var result = StringBuilder()
            var i = 0

            while (n > 0) {
                if (n % 1000 != 0) {
                    result.insert(0, "${dfs(n % 1000)} ${thousands[i]} ")
                }
                n /= 1000
                i++
            }

            return result.toString().trim()
        }

        private fun dfs(num: Int): String {
            if (num == 0) return ""
            if (num < 20) return lessThan20[num]
            if (num < 100) return "${tens[num / 10]} ${lessThan20[num % 10]}".trim()
            return "${lessThan20[num / 100]} Hundred ${dfs(num % 100)}".trim()
        }
    }

}