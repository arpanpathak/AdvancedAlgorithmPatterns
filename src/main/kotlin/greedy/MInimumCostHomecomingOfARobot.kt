package greedy

class MInimumCostHomecomingOfARobot {
    fun minCost(startPos: IntArray, homePos: IntArray, rowCosts: IntArray, colCosts: IntArray): Int {
        var totalCost = 0

        var i = startPos[0]

        while (i != homePos[0]) {
            when {
                i > homePos[0] -> totalCost += rowCosts[--i]
                else -> totalCost += rowCosts[++i]
            }
        }

        i = startPos[1]
        while (i != homePos[1]) {
            when {
                i > homePos[1] -> totalCost += colCosts[--i]
                else -> totalCost += colCosts[++i]
            }
        }

        return totalCost
    }
}