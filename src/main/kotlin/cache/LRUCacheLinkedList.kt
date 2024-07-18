package cache

class LRUCacheLinkedList(private val capacity: Int) {

    private val cache: MutableMap<Int, Node> = HashMap()
    private var head: Node? = null
    private var tail: Node? = null

    fun get(key: Int): Int {
        val node = cache[key] ?: return -1
        removeNode(node)
        addNodeToFront(node)
        return node.value
    }

    fun put(key: Int, value: Int) {
        if (cache.containsKey(key)) {
            val existingNode = cache[key]!!
            existingNode.value = value
            removeNode(existingNode)
            addNodeToFront(existingNode)
        } else {
            if (cache.size >= capacity) {
                removeNode(tail!!)
            }
            val newNode = Node(key, value)
            cache[key] = newNode
            addNodeToFront(newNode)
        }
    }

    private fun addNodeToFront(node: Node) {
        node.prev?.next = node.next
        node.next?.prev = node.prev

        node.prev = null
        node.next = head

        head?.prev = node
        head = node

        if (tail == null) {
            tail = node
        }
    }

    private fun removeNode(node: Node) {
        node.prev?.next = node.next
        node.next?.prev = node.prev

        if (node === head) {
            head = node.next
        }
        if (node === tail) {
            tail = node.prev
        }

        node.prev = null
        node.next = null

        cache.remove(node.key)
    }

    private data class Node(val key: Int, var value: Int, var prev: Node?
