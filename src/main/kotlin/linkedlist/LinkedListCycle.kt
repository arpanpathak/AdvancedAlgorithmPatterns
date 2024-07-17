package linkedlist

class LinkedListCycle {
    fun deleteMiddle(head: ListNode?): ListNode? {
        if (head?.next == null)
            return null

        var currentNode = head
        var prevNode: ListNode? = null
        var index = 0
        var n =0
        while (currentNode != null) {
            n++
            currentNode = currentNode.next

        }

        currentNode = head
        for(i in 0 until n/2 - 1)
            currentNode = currentNode?.next

        currentNode?.next = currentNode?.next?.next

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