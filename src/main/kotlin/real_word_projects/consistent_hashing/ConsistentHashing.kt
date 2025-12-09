import java.util.*

import java.util.*

class ConsistentHash<T : Node>(
    private val replicas: Int = 1
) {
    private val ring: SortedMap<Long, T> = TreeMap()
    private val dataStore: MutableMap<String, MutableMap<String, Any>> = HashMap()

    fun addNode(node: T) {
        val primaryHash = node.id.hashCode().toLong()
        ring[primaryHash] = node

        for (i in 1..replicas) {
            val replicaHash = "${node.id}-replica-$i".hashCode().toLong()
            ring[replicaHash] = node
        }

        // Initialize data storage for this node
        dataStore[node.id] = HashMap()
    }

    fun removeNode(node: T) {
        ring.entries.removeAll { it.value.id == node.id }
        dataStore.remove(node.id)

        // In real system, we'd redistribute the data here
        println("Warning: Data from ${node.id} is now unavailable!")
    }

    fun put(key: String, value: Any): Boolean {
        val node = getNode(key) ?: return false
        dataStore[node.id]?.put(key, value)
        return true
    }

    fun get(key: String): Any? {
        val node = getNode(key) ?: return null
        return dataStore[node.id]?.get(key)
    }

    fun getNode(key: String): T? {
        if (ring.isEmpty()) return null
        val hash = key.hashCode().toLong()
        val tailMap = ring.tailMap(hash)
        return if (tailMap.isEmpty()) ring[ring.firstKey()] else ring[tailMap.firstKey()]
    }

    fun getReplicas(key: String): List<T> {
        if (ring.isEmpty()) return emptyList()
        val hash = key.hashCode().toLong()
        val allNodes = ring.values.toList()
        val index = ring.tailMap(hash).let {
            if (it.isEmpty()) 0 else ring.headMap(it.firstKey()).size
        }
        return (0..replicas).map { offset ->
            allNodes[(index + offset) % allNodes.size]
        }.distinct()
    }
}

interface Node {
    val id: String
}

data class Server(override val id: String) : Node

fun main() {
    val ring = ConsistentHash<Server>(replicas = 2)

    // Add nodes
    val server1 = Server("server1")
    val server2 = Server("server2")
    val server3 = Server("server3")

    ring.addNode(server1)
    ring.addNode(server2)
    ring.addNode(server3)

    // Store data
    ring.put("user1", mapOf("name" to "Alice", "email" to "alice@example.com"))
    ring.put("user2", mapOf("name" to "Bob", "email" to "bob@example.com"))
    ring.put("user3", mapOf("name" to "Charlie", "email" to "charlie@example.com"))

    // Retrieve data
    println("user1 data: ${ring.get("user1")}")
    println("Primary node for user2: ${ring.getNode("user2")?.id}")
    println("All replicas for user1: ${ring.getReplicas("user1").map { it.id }}")
    println("All replicas for user2: ${ring.getReplicas("user2").map { it.id }}")
    println("All replicas for user3: ${ring.getReplicas("user3").map { it.id }}")

    // Remove a node (this would trigger data redistribution in real system)
    ring.removeNode(server2)
    println("After removal, user2 data: ${ring.get("user2")}") // May be unavailable

    // Add new node
    val server4 = Server("server4")
    ring.addNode(server4)
    println("New node added. Primary for user3: ${ring.getNode("user3")?.id}")
}