package linkedlist

class ReverseNodesInKGroups {
    fun reverseKGroup(head: ListNode?, k: Int): ListNode? {
        if (head == null || k == 1) return head

        val dummy = ListNode(0)
        dummy.next = head
        var start: ListNode? = dummy
        var end: ListNode? = dummy

        while (end?.next != null) {
            // Move `end` k steps forward
            for (i in 0 until k) {
                end = end?.next
                if (end == null) return dummy.next // Not enough nodes to reverse
            }

            // Store the next group start
            val nextGroupStart = end?.next

            // Reverse the current group
            val (newStart, newEnd) = reverse(start?.next, end)

            // Connect the reversed group back to the list
            start?.next = newStart
            newEnd?.next = nextGroupStart

            // Move `start` to `newEnd` (which was the original `start` before reversing)
            start = newEnd
            // Set `end` back to `start` for the next iteration
            end = newEnd
        }

        return dummy.next
    }

    private fun reverse(start: ListNode?, end: ListNode?): Pair<ListNode?, ListNode?> {
        var prev: ListNode? = null
        var current = start
        val stop = end?.next

        while (current != stop) {
            val next = current?.next
            current?.next = prev
            prev = current
            current = next
        }

        return Pair(end, start) // `end` is the new start and `start` is the new end after reversing
    }
}