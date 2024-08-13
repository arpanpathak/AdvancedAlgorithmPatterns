package linkedlist

class MergeKSortedList {
    fun mergeTwoLists(list1: ListNode?, list2: ListNode?): ListNode? {
        val head = ListNode(0) // dummy node
        var ptr = head

        var ptr1 = list1
        var ptr2 = list2

        while (ptr1 != null && ptr2 != null) {
            if (ptr1.`val` < ptr2.`val`) {
                ptr.next = ListNode(ptr1.`val`)
                ptr1 = ptr1.next
            } else {
                ptr.next = ListNode(ptr2.`val`)
                ptr2 = ptr2.next
            }
            ptr = ptr.next!!
        }

        ptr.next = ptr1 ?: ptr2

        return head.next
    }

    fun mergeKLists(lists: Array<ListNode?>): ListNode? {
        if (lists.isEmpty())
            return null
        fun merge(start: Int, end: Int): ListNode? {
            if (start == end)
                return lists[start]
            val mid = start + (end - start) / 2
            val left = merge(start, mid)
            val right = merge(mid + 1, end)

            return mergeTwoLists(left, right)
        }
        return merge(0, lists.lastIndex)
    }
}