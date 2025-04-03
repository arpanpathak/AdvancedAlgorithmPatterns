package linkedlist

class LinkedListCycle_II {
    fun detectCycle(head: ListNode?): ListNode? {
        var slow = head
        var fast = head

        // Phase 1: Detect if a cycle exists
        while (fast != null && fast.next != null) {
            slow = slow?.next
            fast = fast.next?.next

            if (slow == fast) {  // Cycle detected
                // Phase 2: Find the start of the cycle
                var entry = head
                while (entry != slow) {
                    entry = entry?.next
                    slow = slow?.next
                }
                return entry
            }
        }

        return null  // No cycle
    }
}
