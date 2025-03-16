package hashtable

class MyHashMap() {
    private val map = Array<Pair<Int, Int>?>(1000000) { null }

    fun put(key: Int, value: Int) {
        val index = key % map.size
        map[index] = Pair(key, value)
    }

    fun get(key: Int): Int {
        val index = key % map.size

        return if (map[index] != null && map[index]?.first == key) {
            map[index]?.second ?: -1
        } else {
            -1
        }
    }

    fun remove(key: Int) {
        val index = key % map.size
        if (map[index]?.first == key) {
            map[index] = null
        }
    }
}
