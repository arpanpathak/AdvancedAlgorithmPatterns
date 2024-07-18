package cache

class LRUCache(private val capacity: Int) {

    private val cache: LinkedHashMap<Int, Int> = LinkedHashMap()

    fun get(key: Int): Int {
        val data = cache[key] ?: -1

        // cache[key] = data // Move accessed element to the end to mark it as recently used
        if (data !=- 1)
            put(key, data)
        return data
    }

    fun put(key: Int, value: Int) {
        if (cache.containsKey(key)) {
            cache.remove(key)
        } else if (cache.size == this.capacity) {
            // Eviction policy
            cache.remove(cache.keys.first()) // Remove the least recently used element
        }
        cache[key] = value // Insert the new element
    }
}