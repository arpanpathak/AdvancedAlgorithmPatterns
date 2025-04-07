package heap

import java.util.*

class SingleThreadedCPU {
    data class Task(val enqueueTime: Int, val processingTime: Int, val index: Int)

    fun getOrder(tasks: Array<IntArray>): IntArray {
        val allTasks = tasks.mapIndexed { i, (enq, proc) -> Task(enq, proc, i) }
            .sortedBy { it.enqueueTime }

        val pq = PriorityQueue(compareBy<Task> { it.processingTime }.thenBy { it.index })
        val result = mutableListOf<Int>()

        var time = 0
        var i = 0

        while (i < allTasks.size || pq.isNotEmpty()) {
            while (i < allTasks.size && allTasks[i].enqueueTime <= time) {
                pq.add(allTasks[i++])
            }

            if (pq.isNotEmpty()) {
                val task = pq.poll()
                time += task.processingTime
                result.add(task.index)
            } else {
                time = allTasks[i].enqueueTime
            }
        }

        return result.toIntArray()
    }
}