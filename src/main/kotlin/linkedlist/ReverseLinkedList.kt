package linkedlist

class ReverseLinkedList {
    fun reverseList(head: ListNode?): ListNode? {
        if (head?.next == null) {
            return head
        }

        val reversedHead = reverseList(head.next)

        head.next?.next = head
        head.next = null

        return reversedHead
    }
}