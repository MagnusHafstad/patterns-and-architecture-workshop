package kaffe.order

import kaffe.model.Customer
import kaffe.model.CustomerType
import kaffe.model.Order
import kaffe.model.PricedOrder
import kaffe.model.SimpleDrink
import kaffe.model.verify

// ─────────────────────────────────────────────────────────────
//  STEP 3 – PLACE THE ORDER                     Pattern: Observer
// ─────────────────────────────────────────────────────────────
//
//  When an order is placed, three things happen: the barista screen shows it,
//  the customer gets loyalty points, and a receipt is sent (see Reactions.kt).
//
//  Every time someone else wants to react to an order, they have to edit OrderService.
//  Now the stats team wants to count how many drinks are sold.
//
//  Your task: change the design so that
//    - the stats team can add a SalesStats that counts drinks sold,
//      WITHOUT changing OrderService
//    - OrderService does not know who reacts to an order, or how many
//
//  The checks at the bottom of main() tell you when you are done.
//  See README.md for hints and a stretch goal.

class OrderService(
    private val baristaScreen: BaristaScreen,
    private val loyalty: LoyaltyProgram,
    private val receipts: ReceiptEmail,
) {
    fun placeOrder(order: PricedOrder) {
        println("Order #${order.order.id} placed")
        baristaScreen.show(order)
        loyalty.addPoints(order)
        receipts.send(order)
    }
}

fun main() {
    // An order that has already been priced (step 2 is done).
    val order = PricedOrder(
        Order(1, Customer("Kari", CustomerType.STUDENT), listOf(SimpleDrink("Latte", 55.0), SimpleDrink("Cappuccino", 45.0))),
        total = 70.0,
    )

    val barista = BaristaScreen()
    val loyalty = LoyaltyProgram()
    val receipts = ReceiptEmail()

    // TODO: when you change the design, set up the OrderService the new way here,
    //       and add your SalesStats.
    val service = OrderService(barista, loyalty, receipts)
    service.placeOrder(order)

    println("Checks for what already works:")
    verify("barista screen showed order #1", barista.shownOrders, listOf(1))
    verify("Kari got 7 loyalty points", loyalty.points["Kari"], 7)
    verify("receipt was sent to Kari", receipts.sentTo, listOf("Kari"))

    println("Checks for your new design:")
    // TODO: uncomment when SalesStats exists.
    // verify("stats counted 2 drinks sold", stats.drinksSold, 2)
}
