package sorting

class HIndex {
    fun hIndex(citations: IntArray): Int {
        // Step 1: Sort the citations in descending order
        citations.sortDescending()

        // Step 2: Find the h-index
        for (i in citations.indices) {
            // The current index represents the number of papers
            // Check if the current number of citations is >= index + 1 (because it's sorted in descending order)
            if (citations[i] < i + 1) {
                return i // This means the h-index is i
            }
        }

        // If no break occurs, then all papers have at least `citations.length` citations
        return citations.size
    }
}