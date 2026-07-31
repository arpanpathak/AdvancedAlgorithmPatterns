package array.Combinatorics

// Warning wrong answer ,  fixing the algorithm
class Permutation_II_Backtracking {
    fun permuteUnique(nums: IntArray): List<List<Int>> {
        val res = ArrayList<List<Int>>()
        val seen = BooleanArray(nums.size)

        fun dfs(path: ArrayList<Int>) {
            if (path.size == nums.size) {
                res.add(path.toList())
                return
            }

            for (i in nums.indices) {
                if (seen[i]) continue

                /**
                 * THE "SYMMETRY BREAKER":
                 * This ensures each duplicate element gets its "fair share" of the slot
                 * in a strictly controlled order.
                 * * If nums[i] is the same as the previous element, and the previous
                 * element (i-1) is NOT currently 'seen' in our path, it means:
                 * 1. We already finished exploring ALL permutations that start with nums[i-1].
                 * 2. If we were to start a new branch with nums[i] now, it would result in
                 * identical permutations to the ones we just finished.
                 * * By skipping here, we ensure that for a set of duplicates like [1, 1, 1],
                 * we only process them in the order: index 0, then index 1, then index 2.
                 */
                if (i != 0 && !seen[i-1] && nums[i] == nums[i-1])
                    continue

                seen[i] = true
                path.add(nums[i])
                dfs(path)
                path.removeAt(path.size - 1)
                seen[i] = false
            }
        }

        nums.sort() // Sort to ensure proper ordering
        dfs(arrayListOf())

        return res
    }
}

// 1,2,3,1

// 1,1,2,3
// 1,1,3,2
// 1,2,3,1
// 1,2,1,3
