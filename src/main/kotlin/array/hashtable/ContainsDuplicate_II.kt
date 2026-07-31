package array.hashtable

class ContainsDuplicate_II {
    fun containsNearbyDuplicate(nums: IntArray, k: Int): Boolean {
        val map = mutableMapOf<Int, Int>()
        for (i in nums.indices) {
            val previousIndex = map[nums[i]]

            previousIndex?.let {  if ( i - it <= k) return true }
            map[nums[i]] = i
        }

        return false
    }
}