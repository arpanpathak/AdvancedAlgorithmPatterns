package binarysearch

class PeakIndexInMountainArray {
    fun peakIndexInMountainArray(arr: IntArray): Int {
        var (left, right) = 0 to arr.lastIndex

        while (left <= right) {
            val mid = left + (right - left) / 2

            when {
                arr[mid + 1] > arr[mid] -> left = mid + 1
                else -> right = mid - 1
            }
        }

        // 0 1   2  3  4  5  6. 7
        // 0, 1, 2, 3, 6, 5, 4, 3

        // left = 0, right = 7 , mid = 3


        return left
    }
}