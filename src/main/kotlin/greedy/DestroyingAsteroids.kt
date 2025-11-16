package greedy

class DestroyingAsteroids {
    fun asteroidsDestroyed(mass: Int, asteroids: IntArray): Boolean {
        var currentMass = mass.toLong()
        asteroids.sort()

        for (asteroidMass in asteroids) {
            if (asteroidMass > currentMass)
                return false
            currentMass += asteroidMass
        }

        return true
    }
}
