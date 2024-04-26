package array.backtracking

class CombinationSum {
    fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun findCombinations(current: MutableList<Int>, start: Int, remaining: Int) {
            when {
                remaining == 0 -> result.add(ArrayList(current))
                remaining > 0 && start < candidates.size -> {
                    current.add(candidates[start])
                    findCombinations(current, start, remaining - candidates[start]) // Include current candidate
                    current.removeLast();
                    findCombinations(current, start + 1, remaining) // Exclude and move to next
                }
            }
        }

        findCombinations(mutableListOf(), 0, target)
        return result
    }
}