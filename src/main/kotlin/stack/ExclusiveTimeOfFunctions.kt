package stack

class ExclusiveTimeOfFunctions {
    fun exclusiveTime(n: Int, logs: List<String>): IntArray {
        val result = IntArray(n)
        val stack = mutableListOf<Int>()
        var prevTime = 0

        for (log in logs) {
            val (id, type, time) = log.split(":")
            val funcId = id.toInt()
            val timestamp = time.toInt()

            when (type) {
                "start" -> {
                    // If there's a function running, accumulate its exclusive time
                    if (stack.isNotEmpty()) {
                        result[stack.last()] += timestamp - prevTime
                    }
                    stack.add(funcId)
                    prevTime = timestamp
                }
                "end" -> {
                    // Pop the function off the stack and update its time
                    result[stack.removeLast()] += timestamp - prevTime + 1
                    prevTime = timestamp + 1
                }
            }
        }

        return result
    }
}
