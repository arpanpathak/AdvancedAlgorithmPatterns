package sorting

import oracle.net.aso.j

class MergeSort {
    fun merge(A: IntArray, L: IntArray, leftCount: Int, R: IntArray, rightCount: Int) {
        var (i, j, k) = listOf(0, 0, 0)

        while (i < leftCount && j < rightCount) {
            if (L[i] < R[j]) A[k++] = L[i++]
            else A[k++] = R[j++]
        }
        while (i < leftCount) A[k++] = L[i++]
        while (j < rightCount) A[k++] = R[j++]
    }

    fun merge_sort(arr: IntArray, n: Int) {
        if (n < 2) return
        val mid = n / 2
        val L = IntArray(mid) { arr[it] }
        val R = IntArray(n - mid) { arr[it + mid] }
        merge_sort(L, mid)
        merge_sort(R, n - mid)
        merge(arr, L, mid, R, n - mid)
    }
}