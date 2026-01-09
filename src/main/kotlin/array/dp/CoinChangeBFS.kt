package array.dp

fun coinChangeBFS(coins: IntArray, amount: Int): Int {
    if (amount == 0) return 0
    val queue = ArrayDeque<Int>().apply { add(0) }
    val visited = mutableSetOf(0)
    var steps = 0

    while (queue.isNotEmpty()) {
        steps++
        repeat(queue.size) {
            val current = queue.removeFirst()
            for (coin in coins) {
                val next = current + coin
                if (next == amount) return steps
                if (next < amount && visited.add(next)) {
                    queue.add(next)
                }
            }
        }
    }
    return -1
}