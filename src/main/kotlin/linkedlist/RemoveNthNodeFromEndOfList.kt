package linkedlist

class RemoveNthNodeFromEndOfList {
    fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
        var size = 0
        var ptr = head

        while(ptr != null) {
            size++
            ptr = ptr.next
        }

        val indexToRemove = (size - n)
        return removeNthFromFront(head, indexToRemove, size)
    }

    private fun removeNthFromFront(head: ListNode?, indexToRemove: Int, size: Int): ListNode? {
        var ptr = head
        var prev: ListNode? = null
        for(i in 0 until indexToRemove) {
            prev = ptr
            ptr = ptr?.next
        }

        return when(indexToRemove) {
            0 -> head?.next
            else -> {
                prev?.next = ptr?.next
                head
            }
        }
    }

    /**
     * Two Pointer approach
     */
    fun removeNthFromEndTwoPointer(head: ListNode?, n: Int): ListNode? {
        val dummy = ListNode(0).apply { next = head }
        var first: ListNode? = dummy
        var second: ListNode? = dummy

        // Move first n + 1 steps ahead
        for (i in 0..n) {
            first = first?.next
        }

        // Move first to the end, maintaining the gap of size n
        while (first != null) {
            first = first.next
            second = second?.next
        }

        // Remove the nth node from the end
        second?.next = second?.next?.next

        return dummy.next
    }
}