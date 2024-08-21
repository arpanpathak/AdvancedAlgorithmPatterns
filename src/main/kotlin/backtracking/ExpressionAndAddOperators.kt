package backtracking

class ExpressionAndAddOperators {
    fun addOperators(num: String, target: Int): List<String> {
        val result = mutableListOf<String>()

        fun dfs(index: Int, prevOperand: Long, currentOperand: Long, value: Long, expression: String) {
            if (index == num.length) {
                if (value == target.toLong() && currentOperand == 0L) {
                    result.add(expression)
                }
                return
            }

            // Extending the current operand by one digit
            val currentDigit = num[index].toString().toLong()
            val newOperand = currentOperand * 10 + currentDigit

            // Avoid cases where numbers start with '0'
            if (newOperand > 0) {
                // No operator added, just extend the current operand
                dfs(index + 1, prevOperand, newOperand, value, expression)
            }

            // Add '+'
            dfs(index + 1, newOperand, 0, value + newOperand, "$expression+$newOperand")

            // Add '-'
            dfs(index + 1, -newOperand, 0, value - newOperand, "$expression-$newOperand")

            // Add '*'
            dfs(index + 1, prevOperand * newOperand, 0, value - prevOperand + (prevOperand * newOperand), "$expression*$newOperand")
        }

        for (i in 1..num.length) {
            val currentOperand = num.substring(0, i).toLong()
            if (currentOperand.toString().length != i) continue // Skip numbers with leading zeros

            dfs(i, currentOperand, 0, currentOperand, currentOperand.toString())
        }

        return result
    }
}


fun main(args: Array<String>) {
    val test = ExpressionAndAddOperators()

    println(test.addOperators("1310", 130))
}