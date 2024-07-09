package linkedlist

class ListNode(var `val`: Int) {
    var next: ListNode? = null
}

class LinkedList {
    fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
        var carry = 0
        val head = ListNode(0)
        var ptr = head
        var (n1, n2) = Pair(l1, l2)

        while (n1!= null || n2 != null) {
            val sum = (n1?.`val` ?: 0) + (n2?.`val` ?: 0) + carry
            ptr.next = ListNode(sum % 10)
            ptr = ptr.next!!

            carry = if (sum > 9) 1 else 0

            if (n1 != null) n1 = n1.next

            if (n2 != null) n2 = n2.next
        }

        if (carry > 0) {
            ptr.next = ListNode(carry)
        }

        return head.next
    }
}