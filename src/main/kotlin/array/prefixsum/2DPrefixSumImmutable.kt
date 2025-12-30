package array.prefixsum

class NumMatrix(matrix: Array<IntArray>) {
    private val prefix: Array<IntArray>

    init {
        val rows = matrix.size
        val cols = matrix[0].size
        // Prefix array is ONE SIZE BIGGER (for safety)
        prefix = Array(rows + 1) { IntArray(cols + 1) }

        for (i in 1..rows) {
            for (j in 1..cols) {
                /*
                WHY THIS FORMULA? Visualize:

                Current cell (blue area):
                  A + B + C + D = total sum up to (i,j)

                Where:
                  A = matrix[i-1][j-1] (current value - GREEN cell)
                  B = prefix[i-1][j]   (area ABOVE - RED rectangle)
                  C = prefix[i][j-1]   (area LEFT - YELLOW rectangle)
                  D = prefix[i-1][j-1] (area CORNER - SUBTRACT because counted twice!)

                ┌─────────┬─────────┐
                │    D    │    B    │
                │ (corner)│  (above)│
                ├─────────┼─────────┤
                │    C    │    A    │
                │  (left) │(current)│
                └─────────┴─────────┘

                So: prefix[i][j] = A + B + C - D
                (We subtract D once because it was added twice in B and C)
                */
                prefix[i][j] = matrix[i-1][j-1] +      // Current cell value
                        prefix[i-1][j] +        // Whole top rectangle
                        prefix[i][j-1] -        // Whole left rectangle
                        prefix[i-1][j-1]        // Top-left corner (subtract duplicate!)
            }
        }
    }

    fun sumRegion(row1: Int, col1: Int, row2: Int, col2: Int): Int {
        /*
        WHY THIS QUERY FORMULA? Visualize:

        We want: sum of ORANGE rectangle = (row1, col1) to (row2, col2)

        ┌──────────────────────────┐
        │         BIG AREA         │
        │  (0,0) to (row2, col2)   │
        │        = prefix[R2+1][C2+1] │
        ├──────────┬───────────────┤
        │  LEFT    │               │
        │  STRIP   │    ORANGE     │
        │          │    (WANT)     │
        ├──────────┼───────────────┤
        │  CORNER  │   TOP STRIP   │
        │ (REMOVE) │   (REMOVE)    │
        └──────────┴───────────────┘

        Steps to get ORANGE area:
        1. Take BIG rectangle: prefix[row2+1][col2+1]
           (from origin to bottom-right)

        2. Remove TOP strip: prefix[row1][col2+1]
           (from origin to just above orange)

        3. Remove LEFT strip: prefix[row2+1][col1]
           (from origin to just left of orange)

        4. Add BACK corner: prefix[row1][col1]
           (because we subtracted it TWICE in steps 2 & 3!)

        Math: ORANGE = BIG - TOP - LEFT + CORNER
        */

        // Convert to prefix indices (add 1 because prefix has offset)
        val R1 = row1 + 1
        val C1 = col1 + 1
        val R2 = row2 + 1
        val C2 = col2 + 1

        return prefix[R2][C2] -   // Big rectangle
                prefix[R1-1][C2] - // Remove top strip (row1-1 = just above)
                prefix[R2][C1-1] + // Remove left strip (col1-1 = just left)
                prefix[R1-1][C1-1] // Add back corner (removed twice!)

        // OR equivalently (using original indices):
        // return prefix[row2+1][col2+1] -
        //        prefix[row1][col2+1] -
        //        prefix[row2+1][col1] +
        //        prefix[row1][col1]
    }
}

// Usage Example
fun main() {
    val matrix = arrayOf(
        intArrayOf(3, 0, 1, 4, 2),
        intArrayOf(5, 6, 3, 2, 1),
        intArrayOf(1, 2, 0, 1, 5),
        intArrayOf(4, 1, 0, 1, 7),
        intArrayOf(1, 0, 3, 0, 5)
    )

    val numMatrix = NumMatrix(matrix)

    // Query: Sum of rectangle from (2,1) to (4,3)
    println(numMatrix.sumRegion(2, 1, 4, 3)) // Output: 8

    // Query: Sum of rectangle from (1,1) to (2,2)
    println(numMatrix.sumRegion(1, 1, 2, 2)) // Output: 11

    // Query: Entire matrix
    println(numMatrix.sumRegion(0, 0, 4, 4)) // Output: 58
}

