package kaffe.solution.order

import kaffe.model.Customer
import kaffe.model.CustomerType
import kaffe.model.Order
import kaffe.model.PricedOrder
import kaffe.model.SimpleDrink
import kaffe.model.verify
import kaffe.order.BaristaScreen
import kaffe.order.LoyaltyProgram
import kaffe.order.ReceiptEmail

// STEP 3 – reference solution (Observer)
//
// OrderService keeps a list of listeners and tells all of them when an order is placed.
// It no longer knows WHO they are. Anyone can subscribe without changing OrderService.

fun interface OrderListener {
    fun orderPlaced(order: PricedOrder)
}

class OrderService {
    private val listeners = mutableListOf<OrderListener>()

    fun subscribe(listener: OrderListener) {
        listeners += listener
    }

    // Stretch goal: unsubscribe.
    fun unsubscribe(listener: OrderListener) {
        listeners -= listener
    }

    fun placeOrder(order: PricedOrder) {
        println("Order #${order.order.id} placed")
        listeners.forEach { it.orderPlaced(order) }
    }
}

// The stats team's new listener. OrderService did not change.
class SalesStats : OrderListener {
    var drinksSold = 0
        private set

    override fun orderPlaced(order: PricedOrder) {
        drinksSold += order.order.drinks.size
        println("  [Stats]    Drinks sold so far: $drinksSold")
    }
}

fun main() {
    val order = PricedOrder(
        Order(1, Customer("Kari", CustomerType.STUDENT), listOf(SimpleDrink("Latte", 55.0), SimpleDrink("Cappuccino", 45.0))),
        total = 70.0,
    )

    val barista = BaristaScreen()
    val loyalty = LoyaltyProgram()
    val receipts = ReceiptEmail()
    val stats = SalesStats()

    val service = OrderService()
    // The existing classes did not have to change: a lambda adapts each of them to OrderListener.
    service.subscribe { barista.show(it) }
    service.subscribe { loyalty.addPoints(it) }
    val receiptListener = OrderListener { receipts.send(it) }
    service.subscribe(receiptListener)
    service.subscribe(stats)

    service.placeOrder(order)

    verify("barista screen showed order #1", barista.shownOrders, listOf(1))
    verify("Kari got 7 loyalty points", loyalty.points["Kari"], 7)
    verify("receipt was sent to Kari", receipts.sentTo, listOf("Kari"))
    verify("stats counted 2 drinks sold", stats.drinksSold, 2)

    // Stretch goal: Kari turns off receipt emails.
    service.unsubscribe(receiptListener)
    service.placeOrder(order.copy(order = order.order.copy(id = 2)))
    verify("stretch: no second receipt after unsubscribe", receipts.sentTo, listOf("Kari"))
    verify("stretch: stats still counting", stats.drinksSold, 4)
}
