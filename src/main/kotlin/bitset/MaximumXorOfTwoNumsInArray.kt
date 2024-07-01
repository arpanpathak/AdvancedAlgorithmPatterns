package bitset

class MaximumXorOfTwoNumsInArray {
    data class Trie(val children: Array<Trie?> = arrayOfNulls(2))

    fun findMaximumXOR(nums: IntArray): Int {
        val root = buildTrie(nums)

        var max = Int.MIN_VALUE
        for (num in nums) {

            var curNode = root
            var curSum = 0
            for (i in 31 downTo 0) {
                val curBit = if ((1 shl i and num) != 0) 1 else 0
                if (curNode.children[curBit xor 1] != null) {
                    curSum += (1 shl i)
                    curNode = curNode.children[curBit xor 1]!!
                } else {
                    curNode = curNode.children[curBit]!!
                }
            }
            max = maxOf(max, curSum)
        }

        return max
    }

    private fun buildTrie(nums: IntArray): Trie {
        val root = Trie()

        for (num in nums) {
            var ptr = root
            for (i in 31 downTo 0) {
                val currBit = if((1 shl i) and num !=0) 1 else 0
                if (ptr.children[currBit] == null) {
                    ptr.children[currBit] = Trie()
                }

                ptr = ptr.children[currBit] !!
            }
        }

        return root
    }
}