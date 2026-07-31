package cache

import java.util.*

class LRUCacheLinkedList(private val capacity: Int) {
    private class Node(val key: Int, var value: Int) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val cache = HashMap<Int, Node>() // Hash map for O(1) access
    private val head = Node(-1, -1) // Dummy head
    private val tail = Node(-1, -1) // Dummy tail

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: Int): Int {
        val node = cache[key] ?: return -1 // Key not found
        moveToHead(node) // Move the node to the head (most recently used)
        return node.value
    }

    fun put(key: Int, value: Int) {
        val node = cache[key]
        if (node != null) {
            // Update the value and move to head
            node.value = value
            moveToHead(node)
        } else {
            // Create a new node and add to head
            val newNode = Node(key, value)
            cache[key] = newNode
            addToHead(newNode)

            // If capacity is exceeded, remove the tail node (least recently used)
            if (cache.size > capacity) {
                val tailNode = removeTail()
                cache.remove(tailNode.key)
            }
        }
    }

    private fun addToHead(node: Node) {
        // Add the node to the head of the list
        node.prev = head
        node.next = head.next
        head.next?.prev = node
        head.next = node
    }

    private fun removeNode(node: Node) {
        // Remove the node from the list
        node.prev?.next = node.next
        node.next?.prev = node.prev
    }

    private fun moveToHead(node: Node) {
        // Move the node to the head of the list
        removeNode(node)
        addToHead(node)
    }

    private fun removeTail(): Node {
        // Remove and return the tail node (least recently used)
        val tailNode = tail.prev!!
        removeNode(tailNode)
        return tailNode
    }
}

fun main() {
    val lruCache = LRUCache(2)
    lruCache.put(1, 1) // Cache is {1=1}
    lruCache.put(2, 2) // Cache is {1=1, 2=2}
    println(lruCache.get(1)) // Returns 1 (Cache is {2=2, 1=1})
    lruCache.put(3, 3) // Evicts key 2, cache is {1=1, 3=3}
    println(lruCache.get(2)) // Returns -1 (not found)
    lruCache.put(4, 4) // Evicts key 1, cache is {3=3, 4=4}
    println(lruCache.get(1)) // Returns -1 (not found)
    println(lruCache.get(3)) // Returns 3 (Cache is {4=4, 3=3})
    println(lruCache.get(4)) // Returns 4 (Cache is {3=3, 4=4})
}
