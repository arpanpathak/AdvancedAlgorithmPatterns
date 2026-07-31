fun subsetsWithDup(nums: IntArray): List<List<Int>> {
  val result = mutableListOf<List<Int>>()
  val current = mutableListOf<Int>()
  nums.sort()
        
  fun backtrack(start: Int) {
    result.add(current.toList())

    for (i in start until nums.size) {
      if (i > start && nums[i-1] == nums[i]) continue

        current.add(nums[i])
        backtrack(i + 1)
        current.removeLast()
      }
    }

    backtrack(0)
    return result
}

fun main() {
    val subsets = subsetsWithDup(intArrayOf(1, 2, 3, 4, 4, 9, -10))
    println(subsets.joinToString(","))
}