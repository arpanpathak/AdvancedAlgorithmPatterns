package string

class ExcelSheetToColumnNumber {
    fun titleToNumber(columnTitle: String): Int {
        var base = 1
        var sum = 0
        for (i in columnTitle.length - 1 downTo 0) {
            sum += (columnTitle[i] - 'A' + 1) * base
            base *= 26
        }
        return sum
    }
}
