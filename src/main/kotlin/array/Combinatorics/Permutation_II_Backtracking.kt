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

                // But why ??
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