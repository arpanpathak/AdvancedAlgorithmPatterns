package linkedlist

class Node(var `val`: Int) {
    var next: Node? = null
}

class InsertIntoASortedCircularLinkedList {
    fun insert(head: Node?, insertVal: Int): Node? {
        val newNode = Node(insertVal)
        if (head == null) return newNode.apply { next = newNode }

        var current: Node? = head
        do {
            when {
                current?.`val`!! <= insertVal && insertVal <= current?.next?.`val`!! -> {
                    newNode.next = current?.next
                    current?.next = newNode
                    return head
                }
                current?.`val`!! > current?.next?.`val`!! && (insertVal >= current?.`val`!! || insertVal <= current?.next?.`val`!!) -> {
                    newNode.next = current?.next
                    current?.next = newNode
                    return head
                }
            }
            current = current?.next
        } while (current != head)

        newNode.next = current?.next
        current?.next = newNode
        return head
    }
}
