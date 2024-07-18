package linkedlist

class LinkedListCycle {
    fun hasCycle(head: ListNode?): Boolean {
        if (head == null || head.next == null) return false

        var slow = head
        var fast = head.next

        while (slow != fast ) {
            if (fast == null || fast.next == null)
                return false

            slow = slow?.next
            fast = fast?.next?.next
        }

        return true
    }
}