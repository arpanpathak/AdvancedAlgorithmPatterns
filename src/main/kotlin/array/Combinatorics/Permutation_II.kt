package array.Combinatorics

class Permutation_II {
    fun swap(nums: IntArray, i: Int, j: Int) {
        nums[j] = nums[i].also { nums[i] = nums[j] }
    }

    fun reverse(nums: IntArray, startIndex: Int) {
        var (left, right) = Pair(startIndex, nums.lastIndex)
        while(left < right) {
            swap(nums, left++, right--)
        }
    }

    fun nextPermutation(nums: IntArray): Boolean {
        var index = nums.lastIndex - 1

        // Find increasing sequence right to left
        // Intuition: By traversing from right to left and finding the first pair where nums[index] < nums[index + 1],
        // we identify the pivot point where the next permutation should change.
        // If no such point exists (i.e., the array is in descending order), the array is the highest permutation
        // and should be reversed to form the lowest permutation.
        while (index >=0 && nums[index] >= nums[index + 1]) {
            index --
        }

        // If all the sequence from right to left
        if (index < 0) {
            reverse(nums, 0)
            return false
        }

        // index is reduced by 1 in while loop. Hence incrementing to get the highest number
        // in this increasing sequence
        index++
        var pivot = nums[index - 1]
        var indexToSwap = index

        // Find index to swap such that nums[indexToSwap] > nums[pivot]
        // Intuition: By finding the smallest element greater than nums[index - 1] from the right part of the array,
        // we ensure that the next permutation is the smallest lexicographical order greater than the current one.
        for (i in index until nums.size) {
            if (nums[i] > pivot) {
                indexToSwap = i
            }
        }

        swap(nums, indexToSwap, index - 1)
        reverse(nums, index)
        return true
    }

    fun permuteUnique(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        nums.sort()
        do {
            result.add(nums.copyOf().toList())
        } while (nextPermutation(nums))

        return result
    }
}