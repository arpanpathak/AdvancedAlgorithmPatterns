package string.greedy

class ShortestWayToFormAString {
    fun shortestWay(source: String, target: String): Int {
        // 1. Initial Check: Create a hash set of source characters.
        val sourceSet = source.toSet()
        target.forEach {
            // If any character in target is not in source, it's impossible.
            if (!sourceSet.contains(it)) {
                return -1
            }
        }

        var count = 0 // Minimum number of source strings required
        var i = 0     // Pointer for the target string (current position to match)
        val N_target = target.length
        val N_source = source.length

        // 2. Greedy Matching
        while (i < N_target) {
            count++          // Start using a new copy of source (increment count)
            val i_start = i  // Save the starting position in target for this pass
            var j = 0        // Pointer for the source string

            // Inner Loop: Try to match as much of the target as possible
            // using ONE pass through the source string (indices 0 to N_source-1).
            while (i < N_target && j < N_source) {
                if (target[i] == source[j]) {
                    // Match found: Advance the target pointer
                    i++
                }
                // Always advance the source pointer
                j++
            }

            // Check for No Progress:
            // If i did not advance at all (i == i_start), it means we failed to match
            // the required character target[i] even though we know it's in source.
            // This happens only if the character is not found in the current pass
            // of the source string (j goes up to N_source) AND i < N_target.
            // This is actually impossible given the initial check passed.
            // A more robust check for a potential infinite loop or a logic issue:
            if (i == i_start) {
                // Should not happen if the initial check passed.
                // If it does, there's an issue (e.g., target has chars not in source)
                // or we're stuck. Return -1 as a safeguard.
                return -1
            }
        }

        return count
    }
}
