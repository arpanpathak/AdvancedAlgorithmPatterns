package probability

class InsertDeleteGetRandom {
    private val elements = mutableListOf<Int>()
    private val elementIndices = mutableMapOf<Int, Int>()

    fun insert(value: Int): Boolean {
        elementIndices[value]?.let { return false }
        elementIndices[value] = elements.size.also { elements.add(value) }
        return true
    }

    fun anotherFunWayToInsert(value: Int): Boolean = when (value) {
        in elementIndices -> false
        else -> {
            elementIndices[value] = elements.size.also { elements.add(value) }
            true
        }
    }

    fun remove(value: Int): Boolean {
        val index = elementIndices[value] ?: return false
        val lastElement = elements.removeLast()

        if (index < elements.size) {
            elements[index] = lastElement
            elementIndices[lastElement] = index
        }
        elementIndices.remove(value)

        return true
    }

    fun getRandom(): Int = elements.random()
}
