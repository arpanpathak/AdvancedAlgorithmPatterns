package array.dp

class MinimumNumberofIncrementsSubarraysFormaTargetArray {
    fun minNumberOperations(target: IntArray): Int {
        var operations = target[0]

        for (i in 1..target.lastIndex) {
            operations += maxOf(0, target[i] - target[i-1])
        }

        return operations
    }
}
