package kaffe.antipattern

import kaffe.antipattern.checkout.Checkout
import kaffe.antipattern.drink.CustomDrink
import kaffe.antipattern.order.OrderService
import kaffe.antipattern.order.SalesStats
import kaffe.model.Customer
import kaffe.model.CustomerType
import kaffe.model.Order
import kaffe.order.BaristaScreen
import kaffe.order.LoyaltyProgram
import kaffe.order.ReceiptEmail

// The whole journey WITHOUT the patterns: drink → price → notify.
// Run it next to solutions/.../FullOrder.kt. The output is the same; the code is not.
// Ask: what would you have to change to add a new add-on, a new campaign, or a new reaction?

fun main() {
    // 1. Build the drinks (flags instead of Decorator)
    val drinks = listOf(
        CustomDrink("Espresso", extraShots = 2, takeaway = true),
        CustomDrink("Espresso", oatMilk = true),
    )
    println("Step 1 - drinks:")
    drinks.forEach { println("  ${it.description()}: ${it.cost()} kr") }

    // 2. Price the order (string switch instead of Strategy)
    val order = Order(42, Customer("Kari", CustomerType.STUDENT), drinks)
    val checkout = Checkout("HAPPY_HOUR_OR_CUSTOMER_PRICE")
    val priced = checkout.checkout(order)
    println("\nStep 2 - price: subtotal ${order.subtotal} kr, Kari pays ${priced.total} kr")

    // 3. Place the order (hard-wired calls instead of Observer)
    val service = OrderService(BaristaScreen(), LoyaltyProgram(), ReceiptEmail(), SalesStats())

    println("\nStep 3 - place the order:")
    service.placeOrder(priced)
}
