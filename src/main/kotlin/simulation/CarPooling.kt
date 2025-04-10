package simulation

class CarPooling {
    fun carPooling(trips: Array<IntArray>, capacity: Int): Boolean {
        var capacity = capacity
        val locations = IntArray(1001)
        for (trip in trips) {
            val (passengers, from, to) = trip

            locations[from] += passengers
            locations[to] -= passengers
        }
        for (p in locations) {
            capacity -= p
            if (capacity < 0) return false
        }
        return true
    }
}
