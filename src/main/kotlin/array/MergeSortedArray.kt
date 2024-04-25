package array

import binarysearch.FindKClosestElements
import kotlin.math.max

class MergeSortedArray {

    fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int): Unit {
        var (x, y, ptr) = listOf(m-1, n-1, m + n - 1)

        while (x >=0  && y > 0) {
            if (nums1[x] > nums2[y] )
                nums1[ptr--] = nums1[x--]
            else
                nums1[ptr--] = nums2[y--]
        }

        while ( y >= 0) {
            nums1[ptr--] = nums2[y--]
        }

        print(nums1.contentToString())
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val testClass = MergeSortedArray()

            // Declaring a 2d Array in Kotlin is tricky
            // Not that Bad. It's still idiomatic and nice....
            var arr = Array(5) { IntArray(6)}


            testClass.merge(intArrayOf(1,5,7,8, 0, 0, 0), 4, intArrayOf(3,6,17),3)
        }
    }
}