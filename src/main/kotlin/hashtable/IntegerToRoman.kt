package hashtable

class IntegerToRoman {
    fun intToRoman(num: Int): String {
        val ROMAN_LITERALS = linkedMapOf(
            1 to "I", 5 to "V", 10 to "X", 50 to "L",
            100 to "C", 500 to "D", 1000 to "M"
        )

        return buildString {
            var remiander = num
            for ((value, numeral)  in ROMAN_LITERALS) {
                while (remiander >= value) {
                    append(numeral)
                        remiander-=num
                }
            }
        }
    }
}