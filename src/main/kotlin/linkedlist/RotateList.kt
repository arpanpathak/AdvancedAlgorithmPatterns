package linkedlist

class RotateList {
    fun rotateRight(head: ListNode?, k: Int): ListNode? {
        if (head == null) return null

        // Find length and tail
        var length = 1
        var tail = head
        while (tail?.next != null) {
            tail = tail.next
            length++
        }

        // Calculate effective rotations needed
        val rotations = k % length
        if (rotations == 0) return head

        // Find new tail (length - rotations - 1 steps from head)
        var newTail = head
        repeat(length - rotations - 1) {
            newTail = newTail?.next
        }

        // Reorganize links
        val newHead = newTail?.next
        newTail?.next = null
        tail?.next = head

        return newHead
    }

    fun rotateRightHeadApproach(head: ListNode?, k: Int): ListNode? {
        if (head?.next == null) return head

        // Find length and make list circular
        var tail = head
        var length = 1
        while (tail?.next != null) {
            tail = tail.next
            length++
        }
        tail?.next = head

        // Find new head position
        val newHeadPos = length - (k % length)
        var newTail = head
        repeat(newHeadPos - 1) {
            newTail = newTail!!.next
        }

        val newHead = newTail!!.next
        newTail?.next = null

        return newHead
    }
}
