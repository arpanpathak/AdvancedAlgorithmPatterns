package heap

import java.util.PriorityQueue
import kotlin.math.abs

class FindKClosestElements {
    class FindKClosestElements {
        /**
         * Solving using Max Heap. O ( N log K + K log K )
         */
        fun findClosestElements(arr: Array<Int>, k: Int, x: Int): List<Int> {
            /**
             * If we create a Max heap returning ( logic(b)  - logic(a) )
             * and keep adding element and remove farthest element in each iteration once heap size goes > K
             * then we'll eliminate (n - k ). Remaining elements will be K closest elements.i.e n - (n-k) = K
             *
             *
             * example of logic function could be frequency count from map eucledian distances
             */
           val priorityQueue = PriorityQueue<Int> { a,b ->
               val diffA = abs(a - x)
               val diffB = abs(b - x)
               if (diffA == diffB) b - a else  diffB - diffA
           }
            arr.forEach { element ->
                priorityQueue.offer(element)
                // Remove the farthest element
                if (priorityQueue.size > k)
                    priorityQueue.poll()
            }
            val result = mutableListOf<Int>()

            repeat(k) {
                if (priorityQueue.isNotEmpty())
                    result.add(priorityQueue.poll())
            }

            return result
        }

        // We could also do

        companion object {
            @JvmStatic
            fun main(args: Array<String>) {
                val testClass = FindKClosestElements()

                println(testClass.findClosestElements(arrayOf(1,2,3,4,5,6,7), 4, 4 ))
            }
        }
    }
}