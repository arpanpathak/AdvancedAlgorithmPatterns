package stack

class OnlineStockSpan {
    data class Stock(var price: Int, var spanDays: Int)

    private val stack = mutableListOf<Stock>()

    fun next(price: Int): Int {
        var spanDays = 1

        while (stack.isNotEmpty() && stack.last().price <= price) {
            spanDays += stack.removeLast().spanDays
        }

        stack.add(Stock(price, spanDays))

        return spanDays
    }
}