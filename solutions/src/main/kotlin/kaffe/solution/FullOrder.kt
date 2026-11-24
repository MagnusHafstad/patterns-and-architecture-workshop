package kaffe.solution

import kaffe.model.Customer
import kaffe.model.CustomerType
import kaffe.model.Order
import kaffe.order.BaristaScreen
import kaffe.order.LoyaltyProgram
import kaffe.order.ReceiptEmail
import kaffe.solution.checkout.Checkout
import kaffe.solution.checkout.CustomerTypePricing
import kaffe.solution.checkout.HappyHour
import kaffe.solution.checkout.bestOf
import kaffe.solution.drink.Espresso
import kaffe.solution.drink.ExtraShot
import kaffe.solution.drink.OatMilk
import kaffe.solution.drink.Takeaway
import kaffe.solution.order.OrderService
import kaffe.solution.order.SalesStats

// The whole journey, for the wrap-up: drink → price → notify.
// Each step uses one pattern, and none of them knows how the others are built.

fun main() {
    // 1. Build the drinks (Decorator)
    val drinks = listOf(
        Takeaway(ExtraShot(ExtraShot(Espresso()))),
        OatMilk(Espresso()),
    )
    println("Step 1 - drinks:")
    drinks.forEach { println("  ${it.description()}: ${it.cost()} kr") }

    // 2. Price the order (Strategy)
    val order = Order(42, Customer("Kari", CustomerType.STUDENT), drinks)
    val checkout = Checkout(bestOf(CustomerTypePricing, HappyHour))
    val priced = checkout.checkout(order)
    println("\nStep 2 - price: subtotal ${order.subtotal} kr, Kari pays ${priced.total} kr")

    // 3. Place the order (Observer)
    val barista = BaristaScreen()
    val loyalty = LoyaltyProgram()
    val receipts = ReceiptEmail()
    val service = OrderService()
    service.subscribe { barista.show(it) }
    service.subscribe { loyalty.addPoints(it) }
    service.subscribe { receipts.send(it) }
    service.subscribe(SalesStats())

    println("\nStep 3 - place the order:")
    service.placeOrder(priced)
}
