package array.Combinatorics

class Combinations {
    fun combine(n: Int, k: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun combine(start: Int, n: Int, k: Int, current: MutableList<Int> = mutableListOf()) {
            if (current.size == k) {
                result.add(current.toList())
                return
            }

            for(i in start..n) {
                current.add(i)
                combine(i + 1, n, k, current)

                current.removeLast()

            }
        }

        combine(1, n,k)
        return result
    }
}