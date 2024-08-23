package string

class ValidWordAbbreviation {
    fun validWordAbbreviation(word: String, abbr: String): Boolean {
        var i = 0
        var j = 0

        while (i < word.length && j < abbr.length) {
            if (abbr[j].isDigit()) {
                if (abbr[j] == '0') return false  // Leading zero check

                var count = 0
                while (j < abbr.length && abbr[j].isDigit()) {
                    count = count * 10 + (abbr[j] - '0')
                    j++
                }
                i += count
            } else {
                if (word[i] != abbr[j]) return false
                i++
                j++
            }
        }

        return i == word.length && j == abbr.length
    }
}
