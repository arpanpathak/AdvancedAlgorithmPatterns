package binarysearch

abstract class VersionControl {
    fun isBadVersion(mid: Int): Boolean {
        return false // Return dummy value
    }

    abstract fun firstBadVersion(n: Int) : Int
}
class FirstBadVersion: VersionControl() {
    override fun firstBadVersion(n: Int) : Int {
        var start = 1
        var end = n

        while (start < end) {
            val mid = start + (end - start) / 2
            when {
                isBadVersion(mid) -> end = mid
                else -> start = mid + 1
            }
        }

        return start
    }
}