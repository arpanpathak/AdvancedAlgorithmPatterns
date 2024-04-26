package array.greedy

class KItemsWithMaximumSum {
    fun kItemsWithMaximumSum(numOnes: Int, numZeros: Int, numNegOnes: Int, k: Int): Int {
        var (K, ones, zeroes, negatives, sum) = listOf( k, numOnes,numZeros, numNegOnes, 0 )
        while (K-- > 0) {
            when {
                ones > 0 -> {
                    sum+= 1
                    ones--
                }
                zeroes == 0 && negatives > 0 ->   {
                    sum-=1
                    negatives--
                }
                else -> zeroes--
            }
        }

        return sum
    }

    // Alternative way
    fun kItemsWithMaximumSumAlternative(numOnes: Int, numZeros: Int, numNegOnes: Int, k: Int): Int {

        if(k <= numOnes)
            return k

        if(k <= numOnes + numZeros)
            return numOnes

        var remainingSum = k - (numOnes + numZeros)

        return numOnes - remainingSum

    }
}