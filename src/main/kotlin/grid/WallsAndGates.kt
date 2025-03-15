package grid

class WallsAndGates {
    fun wallsAndGates(rooms: Array<IntArray>) {
        if (rooms.isEmpty() || rooms[0].isEmpty()) return

        val directions = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)

        val queue = ArrayDeque<Pair<Int, Int>>()

        // Add all gates (0s) to the queue
        for (i in rooms.indices) {
            for (j in rooms[i].indices) {
                if (rooms[i][j] == 0) queue.add(i to j)
            }
        }

        // Perform BFS from all gates
        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeFirst()

            directions.forEach { (dx, dy) ->
                val newX = x + dx
                val newY = y + dy
                // Add empty rooms and update their distance to the nearest gate
                if (newX in rooms.indices && newY in rooms[0].indices && rooms[newX][newY] == Int.MAX_VALUE) {
                    rooms[newX][newY] = rooms[x][y] + 1
                    queue.add(newX to newY)
                }
            }
        }
    }
}