package linkedlist

class SwapNodesInPairs {
    // Example list to dry run this 1 -> 2 -> 3 ->4 -> 5 -> 6
    fun swapPairs(head: ListNode?): ListNode? {
        val dummy = ListNode(0).apply { next = head } // Sentiel node

        // Previous will point to the node before previous being swapped...
        var prev : ListNode? = dummy


        // Loop until there are pair of nodes to swap, i.e
        // prev.next is the first and prev.next?.next is the 2nd node
        while (prev?.next != null && prev.next?.next != null) {
            val (node1, node2) = prev.next to prev.next?.next

            // Swap nodes
            // Step A: Link the node *before* the pair to the second node (node2)
            // (dummy/prev) -> 2
            prev.next = node2

            // Step B: Link the first node (node1) to the node *after* the pair
            // 1 -> 3
            node1?.next = node2?.next
            node2?.next = node1
            prev = node1
        }

        return dummy.next
    }
}