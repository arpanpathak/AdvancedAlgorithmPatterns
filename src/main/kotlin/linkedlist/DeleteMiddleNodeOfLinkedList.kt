package linkedlist

class DeleteMiddleNodeOfLinkedList {
    
    // Two Pass Approach
    fun deleteMiddle(head: ListNode?): ListNode? {
        var currentNode = head
        var prevNode: ListNode? = null
        var index = 0
        var n =0
        while (currentNode != null) {
            n++
            currentNode = currentNode.next

        }

        currentNode = head
        while (currentNode != null) {
            if (index++ == n/2)
                break
            prevNode = currentNode
            currentNode = currentNode.next
        }

        if (prevNode != null)
            prevNode.next = currentNode

        return head
    }

    fun deleteMiddleTwoPointer(head: ListNode?): ListNode? {
        var (slow, fast, prev) = listOf(head, head, null)

        if (head?.next == null)
            return null

        while (fast?.next != null ) {
            prev = slow
            slow = slow?.next
            fast = fast?.next?.next
        }

        prev?.next = slow?.next

        return head
    }
}