package array.greedy

class CanPlaceFlowers {
    fun canPlaceFlowers(flowerbed: IntArray, n: Int): Boolean {
        var flowers = 0
        for (i in 0 until flowerbed.size) {
            val isLeftEmpty = ( i == 0 || flowerbed[i-1] == 0)
            val isRightEmpty = ( i == flowerbed.lastIndex || flowerbed[i+1] == 0 )
            if ( flowerbed[i] == 0 && isLeftEmpty && isRightEmpty ) {
                flowers++
                flowerbed[i] = 1
            }

            if (flowers >= n) {
                return true
            }
        }

        return false
    }
}