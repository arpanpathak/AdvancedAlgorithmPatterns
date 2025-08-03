package linkedlist

class MaximumTwinSumOfALinkedList {
    fun pairSum(head: ListNode?): Int {
        val middle = findMiddle(head)
        val reversed = reverse(middle)

        var maxSum = 0
        var first = head
        var second = reversed

        while (second != null) {
            maxSum = maxOf(maxSum, first!!.`val` + second.`val`)
            first = first.next
            second = second.next
        }

        return maxSum
    }

    private fun findMiddle(head: ListNode?): ListNode? {
        var slow = head
        var fast = head
        while (fast?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }
        return slow
    }

    private fun reverse(head: ListNode?): ListNode? {
        var prev: ListNode? = null
        var current = head
        while (current != null) {
            val next = current.next
            current.next = prev
            prev = current
            current = next
        }
        return prev
    }
}
