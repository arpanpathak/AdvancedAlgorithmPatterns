package linkedlist

class MiddleNode {
    fun middleNode(head: ListNode?): ListNode? {
        if (head == null) return null

        var slow = head
        var fast = head

        while (fast?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }

        return slow
    }
}