package cache

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ShardedLruCache<K, V>(
    private val totalCapacity: Int,
    private val shardCount: Int = 16
) {
    private val shards: Array<LruShard<K, V>>

    init {
        require(totalCapacity > 0) { "Capacity must be positive" }
        require(shardCount > 0 && (shardCount and (shardCount - 1)) == 0) { "Shard count must be a power of 2" }

        val base = totalCapacity / shardCount
        var remainder = totalCapacity % shardCount

        shards = Array(shardCount) {
            val cap = base + if (remainder-- > 0) 1 else 0
            LruShard<K, V>(cap)
        }
    }

    fun get(key: K): V? = shardOf(key).get(key)
    fun put(key: K, value: V) = shardOf(key).put(key, value)
    fun remove(key: K): V? = shardOf(key).remove(key)
    fun clear() = shards.forEach { it.clear() }
    fun size(): Int = shards.sumOf { it.size() }

    private fun shardOf(key: K): LruShard<K, V> {
        val h = key?.hashCode() ?: 0
        // Use spread to prevent poor distribution from weak hashCodes
        val hash = h xor (h ushr 16)
        return shards[hash and (shardCount - 1)]
    }

    private class LruShard<K, V>(private val maxCapacity: Int) {
        private val lock = ReentrantLock()

        private val map = object : LinkedHashMap<K, V>(maxCapacity, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean {
                return size > maxCapacity
            }
        }

        fun get(key: K): V? = lock.withLock { map[key] }

        fun put(key: K, value: V) {
            lock.withLock { map[key] = value }
        }

        fun remove(key: K): V? = lock.withLock { map.remove(key) }

        fun clear() = lock.withLock { map.clear() }

        fun size(): Int = lock.withLock { map.size }
    }
}
