package hashtable

class IntersectionOfTwoArray {
    fun intersection(nums1: IntArray, nums2: IntArray): IntArray {
        val first = nums1.toMutableSet()
        val intersectionSet = mutableSetOf<Int>()

        for (num in nums2) {
            if (first.contains(num))
                intersectionSet.add(num)
        }

        return intersectionSet.toIntArray()
    }

    fun intersectionSet(nums1: IntArray, nums2: IntArray): IntArray {
        return (nums1.toSet() intersect nums2.toSet() ).toIntArray()
    }
}
