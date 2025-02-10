package graph

import java.util.*

class WordLadder {
    fun ladderLength(beginWord: String, endWord: String, wordList: List<String>): Int {
        if (endWord !in wordList) return 0 // if end word is not word bank then it's not possible

        val wordSet = wordList.toHashSet() // Store the word list in a set for O(1) lookups
        val queue: Queue<Pair<String, Int>> = LinkedList() // Queue to store word and level (distance)
        queue.offer(beginWord to 1) // Start with the beginWord and level 1

        while (queue.isNotEmpty()) {
            val (currentWord, level) = queue.poll()

            // Check all possible transformations by changing each character
            for (i in currentWord.indices) {
                val originalChar = currentWord[i]
                for (ch in 'a'..'z') {
                    // Create a new word by replacing the i-th character
                    val newWord = currentWord.substring(0, i) + ch + currentWord.substring(i + 1)

                    // If the new word is the end word, return the result
                    if (newWord == endWord) return level + 1

                    // If the new word is in the word set, we can add it to the queue
                    if (newWord in wordSet) {
                        wordSet.remove(newWord) // Remove to avoid revisiting
                        queue.offer(newWord to level + 1)
                    }
                }
            }
        }

        return 0 // If no transformation is found
    }
}