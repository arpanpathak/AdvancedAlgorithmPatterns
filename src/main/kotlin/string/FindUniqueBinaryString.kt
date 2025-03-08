package string

// Cantor’s Diagonalization Trick
class FindUniqueBinaryString {
    fun findDifferentBinaryString(nums: Array<String>): String {
        val n = nums.size
        val sb = StringBuilder()

        for (i in 0 until n) {
            sb.append(if (nums[i][i] == '0') '1' else '0')
        }

        return sb.toString()
    }
}
