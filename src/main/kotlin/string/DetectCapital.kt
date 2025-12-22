package string

class DetectCapital {
    fun detectCapitalUse(word: String): Boolean {
        var capitals = 0
        for (char in word) {
            if (char.isUpperCase()) capitals++
        }

        return capitals == word.length ||
                capitals == 0 ||
                (capitals == 1 && word[0].isUpperCase())
    }
}