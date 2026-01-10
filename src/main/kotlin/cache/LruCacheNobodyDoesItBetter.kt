package cache

class LruCacheNobodyDoesItBetter(private val capacity: Int) {
    private val cache = LinkedHashMap<Int, Int>(capacity)

    fun get(key: Int): Int = cache.remove(key)?.also {
        cache[key] = it // Re-insert to mark as Most Recently Used
    } ?: -1

    fun put(key: Int, value: Int) {
        when {
            cache.containsKey(key) -> cache.remove(key)
            cache.size == capacity -> cache.remove(cache.keys.first())
        }
        cache[key] = value
    }
}