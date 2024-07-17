package array.backtracking

class CombinationSum3 {
    fun combinationSum3(k: Int, n: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        dfs(k,n, 1, result)
        return result
    }

    fun dfs(
        k: Int,
        remaining: Int,
        start: Int,
        result: MutableList<List<Int>>,
        curr: MutableList<Int> = mutableListOf(),
    ) {

        if (curr.size == k && remaining == 0) {
            result.add(curr.toList())
            return
        }

        if  (remaining <=0 || curr.size == k)
            return

        for (i in start..9) {
            curr.add(i)

            dfs(k, remaining - i, i + 1, result, curr)

            curr.remove(i)
        }

    }
}