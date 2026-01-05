package simulation

class LatestTimeToCatchBus {
    fun latestTimeCatchTheBus(busses: IntArray, passengers: IntArray, capacity: Int): Int {
        busses.sort()
        passengers.sort()

        var lastPassengerIdx = 0
        var lastBusPassengerCount = 0

        // Simulation: Filling the buss
        busses.forEach { busTime ->
            lastBusPassengerCount = 0

            while (lastBusPassengerCount < capacity &&
                   lastPassengerIdx < passengers.size &&
                   passengers[lastPassengerIdx] <= busTime) {
                lastPassengerIdx++
                lastBusPassengerCount++
            }
        }

        var latestPossible = when {
            lastBusPassengerCount < capacity -> busses.last()
            else -> passengers[lastPassengerIdx - 1] // -1 because we already increased the passengerIndex +1
        }

        val passengerSet = passengers.toSet()

        while (passengerSet.contains(latestPossible))
            latestPossible--

        return latestPossible
    }
}