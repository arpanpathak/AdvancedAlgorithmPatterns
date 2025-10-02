package cache

import java.util.HashMap

class LRUCleanAf<Key, Val>(private val capacity: Int) {

    private class CacheNode<Key, Val>(val key: Key, var value: Val) {
        var next: CacheNode<Key, Val>? = null
        var prev: CacheNode<Key, Val>? = null
    }

    private val cache = HashMap<Key, CacheNode<Key, Val>>()
    private var head: CacheNode<Key, Val>? = null
    private var tail: CacheNode<Key, Val>? = null

    init {
        require(capacity > 0) { "Capacity must be a positive integer."}
    }

    // --- Doubly Linked List Operations ---

    private fun addFirst(node: CacheNode<Key, Val>) {
        head?.prev = node
        tail = tail ?: node
        node.next = head
        head = node
    }

    private fun removeNode(node: CacheNode<Key, Val>) {
        node.prev?.next = node.next
        node.next?.prev = node.prev

        if (node == head) {
            head = node.next
        }

        if (node == tail) {
            tail = node.prev
        }
    }

    private fun moveToHead(node: CacheNode<Key, Val>) {
        if (node != head) {
            removeNode(node)
            addFirst(node)
        }
    }

    private fun removeLast(): Key? =
        tail?.let { nodeToRemove ->
            val keyToRemove = nodeToRemove.key
            removeNode(nodeToRemove)
            cache.remove(keyToRemove)
            keyToRemove
        }


    // --- LRU Cache API ---
    fun get(key: Key): Val? =
        cache[key]?.let { node ->
            moveToHead(node)
            node.value
        }

    fun put(key: Key, value: Val) {
        cache[key]?.let {
            it.value = value
            moveToHead(it)
            return
        }

        if (cache.size == capacity) {
            removeLast()
        }

        val newNode = CacheNode(key, value)
        addFirst(newNode)
        cache[key] = newNode
    }
}
