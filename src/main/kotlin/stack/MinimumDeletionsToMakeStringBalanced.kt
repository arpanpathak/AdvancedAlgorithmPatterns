package stack

class MinimumDeletionsToMakeStringBalanced {
    fun minimumDeletions(s: String): Int {
        val stack = ArrayDeque<Char>()
        var deletions = 0

        for (ch in s) {
            if (ch == 'b') {
                stack.addLast('b')
            } else { // ch == 'a'
                if (stack.isNotEmpty() && stack.last() == 'b') {
                    stack.removeLast()      // delete 'b'
                    deletions++      // count this deletion
                }
            }
        }

        return deletions
    }
}
