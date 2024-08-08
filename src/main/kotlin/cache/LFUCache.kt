package cache

class LFUCache(capacity: Int) {

    private val vals = mutableMapOf<Int, Int>()
    private val freq = mutableMapOf<Int, Int>()
    private val lists = mutableMapOf<Int, LinkedHashSet<Int>>()

    private val MAX_SIZE = capacity
    private var min = -1

    init {
        lists[1] = LinkedHashSet()
    }

    fun get(key: Int): Int {
        if (!vals.containsKey(key))
            return -1
        val count = freq[key]!!
        freq[key] = count + 1
        lists[count]?.remove(key)

        if (count == min && lists[count]?.size == 0 ) {
            min++
        }

        if (!lists.containsKey(count  + 1)) {
            lists[count + 1] = LinkedHashSet()
        }

        lists[count + 1]?.add(key)

        return vals[key]!!
    }

    fun put(key: Int, value: Int) {
        if (MAX_SIZE <= 0)
            return ;
        if (vals.containsKey(key)) {
            vals.put(key, value)
            get(key)
            return ;
        }

        if (vals.size >= MAX_SIZE) {
            val evict = lists.get(min)?.first()

            lists.get(min)?.remove(evict)
            vals.remove(evict)
            freq.remove(evict)
        }

        vals[key] = value
        freq[key] = 1
        min = 1
        lists[1]?.add(key)
    }

}