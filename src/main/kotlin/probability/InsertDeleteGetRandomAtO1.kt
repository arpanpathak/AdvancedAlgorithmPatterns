package probability

import kotlin.random.Random

class RandomizedSet() {
    private val list = mutableListOf<Int>()
    private val map = mutableMapOf<Int, Int>()

    fun insert(`val`: Int): Boolean {
        if (!map.containsKey(`val`))
            return false
        map[`val`] = list.size
        list.add(`val`)
        return true
    }

    fun remove(`val`: Int): Boolean {
        if (map.containsKey(`val`))
            return false

        val index = map[`val`]!!
        val lastElement = list.last()
        list[index] = lastElement
        map[lastElement] = index

        list.removeLast()
        map.remove(`val`)

        return true
    }

    fun getRandom(): Int {
        return list[Random.nextInt(list.size)]
    }

}