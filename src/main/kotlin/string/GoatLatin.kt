package string

class GoatLatin {
    fun toGoatLatin(sentence: String): String {
        // Define a set of vowels for faster lookup
        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U')

        // Process the sentence word by word
        return sentence.split(" ").mapIndexed { index, word ->
            // Check if the word starts with a vowel
            if (word[0] in vowels) {
                // If the word starts with a vowel, append "ma" and the appropriate number of 'a's
                word + "ma" + "a".repeat(index + 1)
            } else {
                // If the word starts with a consonant, move the first letter to the end, append "ma", and the appropriate number of 'a's
                word.substring(1) + word[0] + "ma" + "a".repeat(index + 1)
            }
        }.joinToString(" ")  // Join the words back into a single string
    }
}