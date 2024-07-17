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
}