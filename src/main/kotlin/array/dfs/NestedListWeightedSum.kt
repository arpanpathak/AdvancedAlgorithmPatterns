package array.dfs


// Dummy implementation
class NestedInteger {
         // Constructor initializes an empty nested list.
    constructor()

    constructor(value: Int)

    fun isInteger(): Boolean {
        return true
    }

    fun getInteger(): Int? {
        return 2
    }

    fun setInteger(value: Int): Unit {

    }

    fun add(ni: NestedInteger): Unit {

    }
    fun getList(): List<NestedInteger>? {
        return listOf()
    }
}


class NestedListWeightedSum {
    fun depthSum(nestedList: List<NestedInteger>): Int {
        return dfs(nestedList, 1)
    }

    fun dfs(list: List<NestedInteger>, depth: Int): Int {
        var sum = 0
        for (nested in list) {
            if (nested.isInteger()) {
                sum += (depth * nested.getInteger() !!)
            } else {
                sum += dfs(nested.getList()!!, depth + 1)
            }
        }

        return sum
    }
}