package linkedlist

class IntersectionOfTwoLinkedList {
    fun getIntersectionNode(headA: ListNode?, headB: ListNode?): ListNode? {
        if (headA == null || headB == null) return null

        var pA = headA
        var pB = headB

        // Traverse both lists. When one pointer reaches the end, redirect it to the head of the other list.
        while (pA != pB) {
            pA = if (pA == null) headB else pA.next
            pB = if (pB == null) headA else pB.next
        }

        return pA // This will return the intersection node, or null if no intersection.
    }
}
