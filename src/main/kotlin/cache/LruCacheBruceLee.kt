package cache

class LruCacheBruceLee<K, V>(private val capacity: Int) {
    private class Node<K,V>(
        var key: K? = null,
        var value: V? = null,
        var prev: Node<K, V>? = null,
        var next: Node<K,V>? = null
    )

    private val cache = HashMap<K, Node<K, V>>()
    private val head = Node<K, V>() // Dummy Head
    private val tail = Node<K, V>() // Dummy Tail

    init {
        require(capacity > 0) { "Capacity Must be positive" }
        head.next = tail
        tail.prev = head
    }

    fun get(key: K): V? {
        val node = cache[key] ?: return null

        // Move to head if the node is already there
        moveToHead(node)

        return node.value
    }

    fun put(key: K, value: V) {
        cache[key]?.let {
            // Update Value
            it.value = value

            // Move it to head since it's recently accessed
            moveToHead(it)
            return
        }

        if (cache.size >= capacity) {
            val tailNode = removeTail()
            tailNode?.key?.let { cache.remove(it) }
        }

        val newNode = Node(key, value)
        cache[key] = newNode
        addToHead(newNode)
    }

    // Moves node to the front of the linked list to store most recently used item on front
    private fun addToHead(node: Node<K, V>) {
        node.prev = head
        node.next = head.next

        head.next?.prev = node
        head.next = node
    }

    private fun removeNode(node: Node<K, V>?) {
        node?.prev?.next = node?.next
        node?.next?.prev = node?.prev
    }

    private fun moveToHead(node: Node<K, V>) {
        removeNode(node)
        addToHead(node)
    }

    // Remove the least recently used node
    private fun removeTail(): Node<K, V>? {
        val res = tail.prev
        removeNode(res)
        return res
    }
}
