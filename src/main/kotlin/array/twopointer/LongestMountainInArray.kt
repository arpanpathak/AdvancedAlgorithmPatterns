package array.twopointer

class LongestMountainInArray {
    fun longestMountain(arr: IntArray): Int {
        var maxLength = 0
        var i = 1

        while (i < arr.size - 1) {
            if (arr[i - 1] < arr[i] && arr[i] > arr[i + 1]) {
                var left = i - 1
                var right = i + 1

                while (left > 0 && arr[left - 1] < arr[left]) {
                    left--
                }

                while (right < arr.size - 1 && arr[right] > arr[right + 1]) {
                    right++
                }

                maxLength = maxOf(maxLength, right - left + 1)
                i = right
            } else {
                i++
            }
        }

        return maxLength
    }
}
