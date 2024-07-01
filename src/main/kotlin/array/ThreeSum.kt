package array


class ThreeSum {
    fun threeSum(nums: IntArray): List<List<Int>> {
        val result = mutableSetOf<List<Int>>()
        nums.sort()

        for (i in nums.indices) {

            if (i > 0 && nums[i] == nums[i - 1]) continue

            var start = i + 1
            var end = nums.lastIndex

            while (start < end) {
                val sum = nums[start] + nums[end] + nums[i]
                when {
                    sum == 0 -> {
                        result.add(listOf(nums[i], nums[start], nums[end]))

                        start++
                        end--
                    }
                    sum > 0 -> end--
                    else -> start++
                }
            }
        }

        return result.toList()
    }

    // This can be done using set too . Reducing it to 2 sum problem.
}
