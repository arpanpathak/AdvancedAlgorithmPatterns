package linkedlist

class OddEvenLinkedList {
    fun oddEvenList(head: ListNode?): ListNode? {
        var odd = head
        var even = head?.next
        var evenHead = even

        while (even?.next != null) {

            odd?.next = odd?.next?.next
            odd = odd?.next

            even.next = even.next?.next
            even = even.next
        }

        odd?.next = evenHead
        return head
    }

}