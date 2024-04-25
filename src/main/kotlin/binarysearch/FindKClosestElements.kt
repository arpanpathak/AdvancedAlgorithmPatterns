package binarysearch

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class FindKClosestElements {

    //           left
    //            ^
    // arr = [ 1, 2, 3, 4, 5, 6 ], k = 3, x = 4
    fun findClosestElements(arr: IntArray, k: Int, x: Int): List<Int> {
        var left = 0
        var right = arr.size - k
        val result = ArrayList<Int>()

        while (left < right) {
            val mid = left + (right - left) / 2
            if (x - arr[mid] > arr[mid + k] - x) {
                left = mid + 1
            } else {
                right = mid
            }
        }

        for (i in left until left + k) {
            result.add(arr[i])
        }

        return result
    }

    fun findClosestElementsHeap(arr: IntArray, k: Int, x: Int): List<Int> {
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

        return result.sorted()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val testClass = FindKClosestElements()

            println(testClass.findClosestElements(intArrayOf(1,2,3,4,5,6,7), 4, 4 ))
        }
    }
}