package array.prefixsum

class `Minimum NumberofOperationstoMoveAllBallstoEachBox` {
    fun minOperations(boxes: String): IntArray {
        val n = boxes.length
        val result = IntArray(n) { 0 }

        // Left to right pass: Calculate operations considering balls on the left of each box
        // leftMoves = Total number of moves to bring balls from the left
        // leftCount = Total number of balls to the left of the current box
        var (leftMoves, leftCount) = 0 to 0

        for (i in 0 until n) {
            result[i] += leftMoves
            leftCount += boxes[i] - '0'
            leftMoves += leftCount
        }

        // Right to left pass: Calculate operations considering balls on the right of each box
        // Total number of moves to bring balls from the right
        // Total number of balls to the right of the current box
        var (rightMoves ,rightCount) = 0 to 0

        for (i in n - 1 downTo 0) {
            result[i] += rightMoves
            rightCount += boxes[i] - '0'
            rightMoves += rightCount
        }

        return result
    }
}