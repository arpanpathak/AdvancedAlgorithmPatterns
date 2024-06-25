package real_word_projects.dynamic_programming

class CricketResourcesCalculator(var maxOvers: Int, var maxWickets: Int) {
    private val memo: Array<DoubleArray> = Array(maxOvers * 6 + 1) { DoubleArray(maxWickets + 1) { -1.0 } }

    init {
        // Initializing the base conditions for the memoization array
        for (b in 0 until memo.size) {
            memo[b][0] = 0.0  // Assuming no resource when there are balls left but no wickets
        }
        for (w in 0..maxWickets) {
            memo[0][w] = 0.0  // No balls left, so no resources, even if wickets are left
        }
    }

    private fun computeResources(ballsLeft: Int, wicketsRemaining: Int): Double =
        when {
            ballsLeft == 0 || wicketsRemaining == 0 -> memo[ballsLeft][wicketsRemaining]
            memo[ballsLeft][wicketsRemaining] != -1.0 -> memo[ballsLeft][wicketsRemaining]
            else -> {
                val resourceIfNotOut = if (ballsLeft > 0) computeResources(ballsLeft - 1, wicketsRemaining) else 0.0
                val resourceIfOut = if (wicketsRemaining > 0 && ballsLeft > 0) computeResources(ballsLeft - 1, wicketsRemaining - 1) else 0.0
                memo[ballsLeft][wicketsRemaining] = 0.5 * resourceIfNotOut + 0.5 * resourceIfOut
                memo[ballsLeft][wicketsRemaining]
            }
        }

    fun getResource(ballsLeft: Int, wicketsRemaining: Int): Double {
        return computeResources(ballsLeft, wicketsRemaining)
    }

    fun calculateTargetScore(firstInningsScore: Int, oversPlayed: Double, wicketsLost: Int, oversAvailable: Double, wicketsRemaining: Int): Int {
        val ballsPlayed = (oversPlayed * 6).toInt()
        val ballsAvailable = (oversAvailable * 6).toInt()
        val resourcesUsed = getResource(ballsPlayed, maxWickets - wicketsLost)
        val resourcesAvailable = getResource(ballsAvailable, wicketsRemaining)
        val scoreFactor = resourcesAvailable / resourcesUsed
        return Math.ceil(firstInningsScore * scoreFactor).toInt()
    }
}

fun main() {
    val calculator = CricketResourcesCalculator(50, 10)
    val firstInningsScore = 300
    val oversPlayed = 30.5  // 30 overs and 3 balls
    val wicketsLost = 5
    val oversAvailable = 19.2  // 19 overs and 2 balls
    val wicketsRemaining = 7  // Chasing team has 7 wickets left
    val targetScore = calculator.calculateTargetScore(firstInningsScore, oversPlayed, wicketsLost, oversAvailable, wicketsRemaining)
    println("Target score required for the second team with $oversAvailable overs left and $wicketsRemaining wickets remaining: $targetScore")
}
