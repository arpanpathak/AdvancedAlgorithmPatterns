package linkedlist

class PalindromeLinkedList {
    class Solution {
        fun isPalindrome(head: ListNode?): Boolean {
            if (head?.next == null) return true // Edge case: empty or single node

            var slow = head
            var fast = head

            // Step 1: Find the middle
            while (fast?.next != null && fast.next?.next != null) {
                slow = slow?.next
                fast = fast.next?.next
            }

            // Step 2: Reverse second half
            var secondHalf = reverseList(slow?.next)
            var firstHalf = head

            // Step 3: Compare both halves
            var temp = secondHalf
            while (temp != null) {
                if (firstHalf?.`val` != temp.`val`) return false
                firstHalf = firstHalf?.next
                temp = temp.next
            }

            // Step 4: Restore original list (optional)
            slow?.next = reverseList(secondHalf)

            return true
        }

        private fun reverseList(head: ListNode?): ListNode? {
            var prev: ListNode? = null
            var curr = head
            while (curr != null) {
                val next = curr.next
                curr.next = prev
                prev = curr
                curr = next
            }
            return prev
        }
    }
}
