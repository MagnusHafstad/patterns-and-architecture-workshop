package kaffe.order

import kaffe.model.PricedOrder

// The things that react when an order is placed.
// Each one remembers what it did, so main() can check it.
// You may change these classes if your design needs it.

/** Shows new orders to the barista behind the counter. */
class BaristaScreen {
    val shownOrders = mutableListOf<Int>()

    fun show(order: PricedOrder) {
        shownOrders += order.order.id
        println("  [Barista]  Make: ${order.order.drinks.joinToString { it.description() }}")
    }
}

/** Gives the customer 1 loyalty point per 10 kr spent. */
class LoyaltyProgram {
    val points = mutableMapOf<String, Int>()

    fun addPoints(order: PricedOrder) {
        val name = order.order.customer.name
        val earned = (order.total / 10).toInt()
        points[name] = (points[name] ?: 0) + earned
        println("  [Loyalty]  $name earned $earned points")
    }
}

/** Sends the receipt to the customer. */
class ReceiptEmail {
    val sentTo = mutableListOf<String>()

    fun send(order: PricedOrder) {
        val name = order.order.customer.name
        sentTo += name
        println("  [Receipt]  Sent receipt for ${order.total} kr to $name")
    }
}
