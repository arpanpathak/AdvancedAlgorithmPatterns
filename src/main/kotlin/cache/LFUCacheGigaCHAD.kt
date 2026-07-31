package cache

/**
 * A L.F.U(Least Frequently Used Cache) is a type of cache memory
 */
class LFUCacheGigaCHAD<Key, Value> (private val capacity: Int = 20) {
    // Merge value and frequency into a single object to ensure atomicity.
    private data class Entry<Value> (var value: Value, var freq: Int = 1)

    // Primary Storage: O(1) access to Data + Metadata
    private val cache = HashMap<Key, Entry<Value>>()

    // Eviction Storage: O(1) Access to Least Recently used order per frequency count
    private val buckets = HashMap<Int, LinkedHashSet<Key>>()

    private var minFrequency = 0

    init {
        require(capacity > 0) { "Capacity must be greater than 0"}
    }

    fun get(key: Key): Value? {
        val entry = cache[key] ?: return null
        updateFrequency(key, entry)

        return entry.value
    }

    fun put(key: Key, value: Value) {
        // If key exists in the cache then update the value of the entry with the new value and
        // then update its frequency bucket
        cache[key]?.let { entry ->
            entry.value = value
            updateFrequency(key, entry)
            return
        }

        // Cache is full... then evict the one with least frequently used. If there are multiple such elements,
        // evict the least recently used one from the bucket
        if (cache.size >= capacity) {
            buckets[minFrequency]?.firstOrNull()?.let { evictKey ->
                buckets[minFrequency]?.remove(evictKey)
                cache.remove(evictKey)
            }
        }

        // Insert the new key. If the code reaches here it means the key is a fresh key, hence new minFrequency = 1
        cache[key] = Entry(value)
        minFrequency = 1

        buckets.getOrPut(1) { linkedSetOf() }.add(key)
    }

    private fun updateFrequency(key: Key, entry: Entry<Value>) {
        buckets[entry.freq]?.remove(key)

        if (entry.freq == minFrequency && buckets[entry.freq]!!.isEmpty()) {
            minFrequency++
            buckets.remove(entry.freq) // Nuke the linked hash set from memory if bucket is empty
        }

        entry.freq++
        buckets.getOrPut(entry.freq) { linkedSetOf() }.add(key)

    }
}
