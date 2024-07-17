package binarysearch

/**
 * The API guess is defined in the parent class.
 * @param  num   your guess
 * @return 	     -1 if num is higher than the picked number
 *			      1 if num is lower than the picked number
 *               otherwise return 0
 * fun guess(num:Int):Int {}
 */

class Solution:GuessGame() {
    override fun guessNumber(n:Int):Int {
        var start = 1
        var end = n

        while (start <= end) {
            val mid = start + (end-start) / 2

            val distance = guess(mid)


            when {
                distance == 0 -> return mid
                distance < 0 -> end = mid - 1
                else -> start = mid + 1
            }
        }

        return -1
    }


}

open class GuessGame {

    open fun guessNumber(n: Int): Int {
        TODO("Not yet implemented")
    }

    fun guess(mid: Int): Any {
        return 0
    }
}
