package kaffe.antipattern.order

import kaffe.model.Customer
import kaffe.model.CustomerType
import kaffe.model.Order
import kaffe.model.PricedOrder
import kaffe.model.SimpleDrink
import kaffe.model.verify
import kaffe.order.BaristaScreen
import kaffe.order.LoyaltyProgram
import kaffe.order.ReceiptEmail

// STEP 3 – antipattern solution (no Observer)
//
// The "just make the checks pass" approach: the stats team opens OrderService
// and adds themselves as one more constructor parameter and one more line.
//
// It works, and every check passes. Compare it with solutions/.../order/OrderService.kt:
//   - The task said "WITHOUT changing OrderService". We changed it. Next team will too.
//   - OrderService now depends on code from four teams. A change in any of them can break it.
//   - Every place that creates an OrderService (tests, the till, FullOrder) had to change,
//     even if it doesn't care about stats.
//   - Each reaction has its own method name (show, addPoints, send, count), so nothing can treat them alike.
//   - "Unsubscribe" (stretch) is a special-case flag for receipts only. Want to turn off loyalty? Another flag.

class OrderService(
    private val baristaScreen: BaristaScreen,
    private val loyalty: LoyaltyProgram,
    private val receipts: ReceiptEmail,
    private val stats: SalesStats, // NEW: added by the stats team
) {
    var receiptsEnabled = true // NEW (stretch): "unsubscribe", but only for receipts

    fun placeOrder(order: PricedOrder) {
        println("Order #${order.order.id} placed")
        baristaScreen.show(order)
        loyalty.addPoints(order)
        if (receiptsEnabled) receipts.send(order)
        stats.count(order) // NEW
    }
}

class SalesStats {
    var drinksSold = 0
        private set

    fun count(order: PricedOrder) {
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

    val service = OrderService(barista, loyalty, receipts, stats)
    service.placeOrder(order)

    verify("barista screen showed order #1", barista.shownOrders, listOf(1))
    verify("Kari got 7 loyalty points", loyalty.points["Kari"], 7)
    verify("receipt was sent to Kari", receipts.sentTo, listOf("Kari"))
    verify("stats counted 2 drinks sold", stats.drinksSold, 2)

    // Stretch: Kari turns off receipt emails. Note: this turns them off for EVERY customer.
    service.receiptsEnabled = false
    service.placeOrder(order.copy(order = order.order.copy(id = 2)))
    verify("stretch: no second receipt after unsubscribe", receipts.sentTo, listOf("Kari"))
    verify("stretch: stats still counting", stats.drinksSold, 4)
}
