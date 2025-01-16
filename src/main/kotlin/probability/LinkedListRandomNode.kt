package probability

import linkedlist.ListNode
import kotlin.random.Random

class LinkedListRandomNode (private val head: ListNode?) {
    fun getRandom(): Int {
        var (count, result) = 0 to 0
        var ptr = head

        while (ptr != null) {
            count++

            if (Random.nextInt(count) == 0) {
                result = ptr.`val`
            }

            ptr = ptr.next
        }

        return result
    }

    // Another possible solution
    fun getRandom_2(): Int {
        var current = head
        var result = current?.`val` ?: throw IllegalArgumentException("List is empty")
        var count = 0

        while (current != null) {
            count++
            // Select the current node if Random.nextInt(count) == count - 1
            if (Random.nextInt(count) == count - 1) {
                result = current.`val`
            }
            current = current.next
        }

        return result
    }
}