package array.hashtable

class FindMissingPositive {
    fun firstMissingPositive(A: IntArray): Int {
        for (i in A.indices) {
            var current = A[i]

            while (current in 1..A.size && A[current-1] != current) {
                val next = A[current - 1]
                A[current - 1] = current
                current = next
            }
        }

        for (i in A.indices)
            if (A[i] != i + 1)
                return i + 1

        return A.size + 1
    }
}