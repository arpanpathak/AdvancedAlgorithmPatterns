package array.hashtable

import java.util.*

class SnapshotArray(length: Int) {
    private var snapId = 0
    private val historyRecords = Array(length) { TreeMap<Int, Int>().apply { put(0, 0) } }

    // Sets the value at the given index for the current snapId.
    fun set(index: Int, `val`: Int) {
        historyRecords[index][snapId] = `val`
    }

    // Takes a snapshot and returns the snapId.
    fun snap(): Int {
        return snapId++
    }

    // Gets the value at the given index for the specific snapId.
    fun get(index: Int, snapId: Int): Int {
        // Find the largest snapId <= given snapId using floorEntry
        return historyRecords[index].floorEntry(snapId)?.value ?: 0
    }
}
