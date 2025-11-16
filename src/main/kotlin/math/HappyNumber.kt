package math

class HappyNumber {
    private fun getNext(n: Int): Int {
        var totalSum = 0
        var num = n

        while (num > 0) {
            val digit = num % 10
            num /= 10
            totalSum += digit * digit
        }
        return totalSum
    }

    fun isHappy(n: Int): Boolean {
        val seen = hashSetOf<Int>()
        var currentNumber = n

        while (currentNumber != 1 && !seen.contains(currentNumber)) {
            seen.add(currentNumber.also { currentNumber = getNext(it) })
        }

        return currentNumber == 1
    }

    fun isHappyFloydCycle(n: Int): Boolean {
        var slow = n
        var fast = getNext(n) // Fast pointer starts one step ahead

        // The condition for the loop is that the pointers haven't met
        // AND the fast pointer hasn't reached 1 yet.
        while (fast != 1 && slow != fast) {
            slow = getNext(slow)          // Slow moves 1 step
            fast = getNext(getNext(fast)) // Fast moves 2 steps
        }

        // If the fast pointer reached 1, the number is happy.
        return fast == 1
    }
}
