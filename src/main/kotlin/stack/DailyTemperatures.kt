package stack

class DailyTemperatures {
    fun dailyTemperatures(temperatures: IntArray): IntArray {
        val result = IntArray(temperatures.size) { 0 }
        val stack = mutableListOf<Int>()

        for (i in temperatures.indices) {
            while (stack.isNotEmpty() && temperatures[i] > temperatures[stack.last()]) {
                val idx = stack.removeLast()
                result[idx] = i - idx
            }

            stack.add(i)
        }

        return result
    }
}