package simulation

class RobotBoundedInCircle {
    fun isRobotBounded(instructions: String): Boolean {
        var (x, y) = 0 to 0
        var dir = 0
        val directions = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)

        for (c in instructions) when (c) {
            'G' -> { x += directions[dir].first; y += directions[dir].second }
            'L' -> dir = (dir + 3) % 4
            'R' -> dir = (dir + 1) % 4
        }

        return (x == 0 && y == 0) || (dir != 0)
    }
}