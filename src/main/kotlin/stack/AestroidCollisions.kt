package stack

class AestroidCollisions {
    fun asteroidCollision(asteroids: IntArray): IntArray {
        val stack = mutableListOf<Int>()

        for (speed in asteroids) {
            if (speed > 0 ) {
                stack.add(speed)
                continue
            }

            while ( stack.isNotEmpty() && stack.last() > 0 && stack.last() < -speed)
                stack.removeLast()

            if (stack.isEmpty() || stack.last() < 0)
                stack.add(speed)
            else if(stack.last() == -speed)
                stack.removeLast()

        }

        return stack.toIntArray()
    }
}