package numbers

class PalindromeNumber {
    fun isPalindrome(x: Int): Boolean {
        var (reduced, sum) = listOf(x, 0, 1)

        while (reduced > 0)  {
            sum = sum * 10 + reduced % 10
            reduced /= 10
        }
        return sum == x
    }
}
