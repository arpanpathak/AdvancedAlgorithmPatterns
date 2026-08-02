package graph.topological_sort

fun main() {
    val someList = listOf(1,2,3,4,44)
    val matrix = arrayOf(
        intArrayOf(1,0,1,1,0,1),
        intArrayOf(1,0,1,1,0,1),
        intArrayOf(1,0,1,1,0,1),
        intArrayOf(1,0,1,1,0,1),
        intArrayOf(1,1,1,1,0,1),
        intArrayOf(1,1,1,1,0,1),
    )

    someList.fold(-10) { accum, elem -> accum + elem }

    val prefixSumArray = someList.runningFold(0) { runningSum, elem -> runningSum + elem }

    val dp = IntArray(6)

    matrix.fold(dp) { accumHeight, row->
        row.forEachIndexes { index, item ->  }
    }

}