package string

import java.util.ArrayDeque

class CountNumberOfWordsWhichAreSubSequence {

    private data class WordState(val wordIndex: Int, val charIndex: Int)

    fun numMatchingSubseq(s: String, words: List<String>): Int {
        val waiting: Array<ArrayDeque<WordState>> = Array(26) { ArrayDeque<WordState>() }

        words.forEachIndexed { wordIndex, word ->
            if (word.isNotEmpty()) {
                val firstChar = word[0]
                waiting[firstChar - 'a'].addLast(WordState(wordIndex, 0))
            }
        }

        var count = 0

        for (c in s) {
            val bucketIndex = c - 'a'
            val currentQueue = waiting[bucketIndex]

            repeat(currentQueue.size) {
                val state = currentQueue.removeFirst()

                val wordIndex = state.wordIndex
                val nextCharIndex = state.charIndex + 1

                val word = words[wordIndex]

                if (nextCharIndex == word.length) {
                    count++
                } else {
                    val nextChar = word[nextCharIndex]
                    waiting[nextChar - 'a'].addLast(WordState(wordIndex, nextCharIndex))
                }
            }
        }

        return count
    }
}

