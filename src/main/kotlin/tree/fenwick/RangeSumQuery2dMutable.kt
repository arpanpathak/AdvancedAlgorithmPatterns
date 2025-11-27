package tree.fenwick

class RangeSumQuery2dMutable {
    class NumMatrix(matrix: Array<IntArray>) {
        // M and N are the dimensions of the original matrix
        private val M: Int
        private val N: Int

        // bit is the 2D Binary Indexed Tree (BIT) for prefix sums.
        // It is 1-indexed, so size is (M+1) x (N+1).
        private val bit: Array<IntArray>

        // originalMatrix stores the current value of each cell.
        // We need this to calculate the delta when 'update' is called.
        private val originalMatrix: Array<IntArray>

        init {
            // Handle edge case of empty matrix
            if (matrix.isEmpty() || matrix[0].isEmpty()) {
                M = 0
                N = 0
                bit = emptyArray()
                originalMatrix = emptyArray()
            } else {
                M = matrix.size
                N = matrix[0].size
                // Initialize BIT and original value array
                bit = Array(M + 1) { IntArray(N + 1) }
                originalMatrix = Array(M) { IntArray(N) }

                // Initialize the BIT by adding all initial elements
                for (r in 0 until M) {
                    for (c in 0 until N) {
                        // Update the BIT with the value from the original matrix
                        // This uses the private addDelta function
                        update(r, c, matrix[r][c])
                    }
                }
            }
        }

        /**
         * Internal helper function to update the BIT structure by adding a delta value.
         * Takes 0-indexed r, c, but uses r+1, c+1 internally for 1-indexing.
         * Time Complexity: O(log M * log N)
         */
        private fun addDelta(r: Int, c: Int, delta: Int) {
            var i = r + 1 // 1-indexed row
            while (i <= M) {
                var j = c + 1 // 1-indexed column
                while (j <= N) {
                    bit[i][j] += delta
                    // j & -j gives the last set bit (LSB)
                    j += (j and -j)
                }
                i += (i and -i)
            }
        }

        /**
         * Updates the value of the cell (row, col) to 'val'.
         * Time Complexity: O(log M * log N)
         */
        fun update(row: Int, col: Int, `val`: Int) {
            // If row or col are out of bounds (or matrix is empty), just return
            if (M == 0 || N == 0) return

            // 1. Calculate the change (delta)
            val delta = `val` - originalMatrix[row][col]

            // 2. Update the original matrix value
            originalMatrix[row][col] = `val`

            // 3. Update the BIT with the delta
            addDelta(row, col, delta)
        }

        /**
         * Internal helper function to query the prefix sum S(r, c).
         * Returns the sum of the rectangle from (0, 0) to (r, c) inclusive.
         * Takes 0-indexed r, c, but uses r+1, c+1 internally for 1-indexing.
         * Time Complexity: O(log M * log N)
         */
        private fun queryPrefixSum(r: Int, c: Int): Int {
            if (r < 0 || c < 0) return 0 // Base case for inclusion-exclusion

            var sum = 0
            var i = r + 1 // 1-indexed row
            while (i > 0) {
                var j = c + 1 // 1-indexed column
                while (j > 0) {
                    sum += bit[i][j]
                    // j & -j gives the last set bit (LSB)
                    j -= (j and -j)
                }
                i -= (i and -i)
            }
            return sum
        }

        /**
         * Returns the sum of the elements within the rectangle defined by
         * its upper left corner (row1, col1) and lower right corner (row2, col2).
         * Time Complexity: O(log M * log N)
         */
        fun sumRegion(row1: Int, col1: Int, row2: Int, col2: Int): Int {
            // Use the Inclusion-Exclusion Principle:
            // Sum(r1,c1 to r2,c2) = S(r2, c2) - S(r1-1, c2) - S(r2, c1-1) + S(r1-1, c1-1)

            val total = queryPrefixSum(row2, col2)
            val excludeTop = queryPrefixSum(row1 - 1, col2)
            val excludeLeft = queryPrefixSum(row2, col1 - 1)
            val includeTopLeft = queryPrefixSum(row1 - 1, col1 - 1)

            return total - excludeTop - excludeLeft + includeTopLeft
        }
    }
}