package array.prefixsum


class SubArraySumsDivisibleByK {
    fun subarraysDivByK(A: IntArray, K: Int): Int {
        var ans = 0
        var sum = 0
        val hm = mutableMapOf<Int, Int>()
        hm[0] = 1

        for (num in A) {
            sum += num
            val rem = Math.floorMod(sum, K)
            ans += hm.getOrDefault(rem, 0)
            hm[rem] = hm.getOrDefault(rem, 0) + 1
        }

        return ans
    }
}