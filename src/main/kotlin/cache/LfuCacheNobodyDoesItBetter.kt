package cache

class LfuCacheNobodyDoesItBetter(private val capacity: Int) {
    private val vals = mutableMapOf<Int, Int>()
    private val freq = mutableMapOf<Int, Int>()
    private val lists = mutableMapOf<Int, LinkedHashSet<Int>>()
    private var minFreq = -1

    fun get(key: Int): Int = vals[key]?.also { updateFreq(key) } ?: -1

    fun put(key: Int, value: Int) {
        when {
            capacity <= 0 -> return
            vals.containsKey(key) -> {
                vals[key] = value
                updateFreq(key)
            }
            else -> {
                if (vals.size >= capacity) evict()

                // Insert new node
                vals[key] = value
                freq[key] = 1
                minFreq = 1
                lists.getOrPut(1) { LinkedHashSet() }.add(key)
            }
        }
    }

    private fun evict() {
        lists[minFreq]?.firstOrNull()?.let { keyToEvict ->
            lists[minFreq]?.remove(keyToEvict)
            vals.remove(keyToEvict)
            freq.remove(keyToEvict)
        }
    }

    private fun updateFreq(key: Int) {
        val currentFreq = freq[key] ?: return
        val nextFreq = currentFreq + 1

        freq[key] = nextFreq
        lists[currentFreq]?.remove(key)

        if (currentFreq == minFreq && lists[currentFreq]?.isEmpty() == true) {
            minFreq++
        }

        lists.getOrPut(nextFreq) { LinkedHashSet() }.add(key)
    }
}
