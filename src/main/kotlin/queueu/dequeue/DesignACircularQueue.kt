package queueu.dequeue

class DesignACircularQueue {
    class MyCircularQueue(k: Int) {
        private val queue = IntArray(k)
        private var front = 0
        private var rear = 0
        private var size = 0
        private val capacity = k

        fun enQueue(value: Int): Boolean {
            if (isFull()) {
                return false
            }
            queue[rear] = value
            rear = (rear + 1) % capacity
            size++
            return true
        }

        fun deQueue(): Boolean {
            if (isEmpty()) {
                return false
            }
            front = (front + 1) % capacity
            size--
            return true
        }

        fun Front(): Int {
            if (isEmpty()) {
                return -1
            }
            return queue[front]
        }

        fun Rear(): Int {
            if (isEmpty()) {
                return -1
            }
            return queue[(rear - 1 + capacity) % capacity]
        }

        fun isEmpty(): Boolean {
            return size == 0
        }

        fun isFull(): Boolean {
            return size == capacity
        }
    }
}
