package linkedlist

class InsertIntoASortedCircularList {
    fun insert(head: Node?, insertVal: Int): Node? {
        val newNode = Node(insertVal)
        if (head == null)
            return newNode.also { it.next = it }
        var prev = head
        var cur = head.next

        while (cur != head) {
            if (prev!!.`val` <= insertVal && insertVal <= cur!!.`val`) {
                break
            }

            if (prev.`val` > cur!!.`val` && (insertVal >= prev.`val` || insertVal <= cur.`val`))
                break
            cur = cur.next
            prev = prev.next
        }

        prev?.next = newNode
        newNode.next = cur

        return head
    }
}