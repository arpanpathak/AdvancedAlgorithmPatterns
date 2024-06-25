package greedy

import jdk.internal.org.jline.utils.Colors.s

class CarFleet {
    data class Car (val position: Double, val eta: Double)

    fun carFleet(target: Int, position: IntArray, speed: IntArray): Int {
        var ( fleets, N ) = listOf(0, position.size)
        val cars = mutableListOf<Car>()

        // s= vt => t = s / v
        position.forEachIndexed{
                i,s -> cars.add(Car(s.toDouble(), ((target - s).toDouble()/speed[i].toDouble())))
        }

        // Sort by starting position in descending order. The cars close to target will reach faster.
        // The car which is closest to target will always leed the fleet. So sorting in descending order of position ( or ascending order of ETA )
        cars.sortBy {  -it.position }

        var currentSlowestEta = 0.0
        cars.forEach { car ->
            // If ETA of any cut is slower than current ETA then definitely this car will not collide with current slowest car and lead a fleet
            if (car.eta > currentSlowestEta) {
                fleets ++
                currentSlowestEta = car.eta
            }
        }

        return fleets
    }
}