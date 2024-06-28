package binarysearch

// These are the challenges that I need to somewhoe solve to get
class MedianOfTwoSortedARrays {
    fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {

        if (nums1.size > nums2.size ) {
            return findMedianSortedArrays(nums2, nums1)
        }

        val m = nums1.size
        val n = nums2.size
        var start = 0
        var end = m

        while (start <= end) {
            val partitionX = (start + end) / 2
            val partitionY = (m + n + 1) / 2 - partitionX

            // Use getOrNull with the Elvis operator to handle boundaries
            val leftX = nums1.getOrNull(partitionX - 1) ?: Int.MIN_VALUE
            val rightX = nums1.getOrNull(partitionX) ?: Int.MAX_VALUE

            val leftY = nums2.getOrNull(partitionY - 1) ?: Int.MIN_VALUE
            val rightY = nums2.getOrNull(partitionY) ?: Int.MAX_VALUE

            // Check if we have found the correct partition
            when {
                leftX <= rightY && leftY <= rightX -> {
                    return if ((m + n) % 2 == 0) {
                        (maxOf(leftX, leftY) + minOf(rightX, rightY)) / 2.0
                    } else {
                        maxOf(leftX, leftY).toDouble()
                    }
                }
                leftX > rightY -> end = partitionX - 1
                else -> start = partitionX + 1
            }
        }
        return -1.0
    }
}
