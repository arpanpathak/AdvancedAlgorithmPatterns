package bitset

import java.util.*

class FirstLetterToAppearTwice {
    /**
     * This is more memory efficient approach...
     */
    fun repeatedCharacter(s: String): Char {

        var bits = 0
        for (ch in s) {
            val ascii = ch - 'a'
            val bit = 1 shl ascii

            if ( (bits and bit) >= 1 ) {
                return ch
            }

            bits = bits or bit

        }

        return 'x'
    }

    fun repeatedCharacterBitset(s: String): Char {

        val seen = BitSet(26)  // BitSet for 26 lowercase English letters

        for (ch in s) {
            val index = ch - 'a'  // Calculate the index for the character

            if (seen.get(index)) {  // Check if the bit at index is already set
                return ch
            }

            seen.set(index)  // Set the bit at index
        }

        return 'x'  // Return null if no repeated character is found
    }
}