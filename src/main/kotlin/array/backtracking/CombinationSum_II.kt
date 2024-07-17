package array.backtracking

class CombinationSum_II {
    fun combinationSum2(candidates: IntArray, target: Int): List<List<Int>> {
        val results = mutableListOf<List<Int>>()
        candidates.sort()

        fun dfs(index: Int, target: Int, curr: MutableList<Int>) {
            if (target == 0) {
                results.add(ArrayList(curr))
                return
            }

            for (i in index until candidates.size) {
                if (i > index && candidates[i] == candidates[i - 1]) continue // Skip duplicates
                if (candidates[i] > target) break // Prune the search

                curr.add(candidates[i])
                dfs(i + 1, target - candidates[i], curr)
                curr.removeAt(curr.size - 1)
            }
        }

        dfs(0, target, mutableListOf())
        return results
    }
}