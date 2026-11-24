package kaffe.antipattern.checkout

import kaffe.model.Customer
import kaffe.model.CustomerType
import kaffe.model.Order
import kaffe.model.PricedOrder
import kaffe.model.SimpleDrink
import kaffe.model.verify

// STEP 2 – antipattern solution (no Strategy)
//
// The "just make the checks pass" approach: give Checkout a campaign name and add a branch per campaign.
//
// It works, and every check passes. Compare it with solutions/.../checkout/Checkout.kt:
//   - Every weekly campaign means editing Checkout, the class that handles everyone's money.
//   - Campaigns are Strings. A typo like "HAPPYHOUR" compiles, then crashes at the till.
//   - Combining campaigns (stretch: best of) needs its own hand-written branch for every combination,
//     with the happy hour math copy-pasted into it. Two campaigns: 1 combo. Five campaigns: 26.
//   - You can't test happy hour on its own: it only exists inside Checkout.
//   - Dead campaigns pile up as branches, because nobody dares to delete them.

class Checkout(private val campaign: String = "NONE") {
    fun checkout(order: Order): PricedOrder {
        val customerPrice = when (order.customer.type) {
            CustomerType.REGULAR -> order.subtotal
            CustomerType.STUDENT -> order.subtotal * 0.8
            CustomerType.EMPLOYEE -> order.subtotal * 0.5
        }

        val total = when (campaign) {
            "NONE" -> customerPrice
            "HAPPY_HOUR" -> order.subtotal * 0.7
            "20_KR_OFF" -> maxOf(order.subtotal - 20.0, 0.0)                     // stretch 1: one more branch
            "HAPPY_HOUR_OR_CUSTOMER_PRICE" -> minOf(customerPrice, order.subtotal * 0.7) // stretch 2: copy-pasted 0.7
            // "20_KR_OFF_OR_CUSTOMER_PRICE" -> ...
            // "HAPPY_HOUR_OR_20_KR_OFF" -> ...
            // "HAPPY_HOUR_OR_20_KR_OFF_OR_CUSTOMER_PRICE" -> ...
            else -> error("Unknown campaign: $campaign")
        }
        return PricedOrder(order, total)
    }
}

fun main() {
    val drinks = listOf(SimpleDrink("Latte", 55.0), SimpleDrink("Cappuccino", 45.0))
    val regular = Order(1, Customer("Ola", CustomerType.REGULAR), drinks)
    val student = Order(2, Customer("Kari", CustomerType.STUDENT), drinks)
    val employee = Order(3, Customer("Nora", CustomerType.EMPLOYEE), drinks)

    val normalCheckout = Checkout()
    verify("normal: regular pays 100 kr", normalCheckout.checkout(regular).total, 100.0)
    verify("normal: student pays 80 kr", normalCheckout.checkout(student).total, 80.0)
    verify("normal: employee pays 50 kr", normalCheckout.checkout(employee).total, 50.0)

    val happyHourCheckout = Checkout("HAPPY_HOUR")
    verify("happy hour: regular pays 70 kr", happyHourCheckout.checkout(regular).total, 70.0)
    verify("happy hour: student pays 70 kr", happyHourCheckout.checkout(student).total, 70.0)

    verify("stretch: 20 kr off", Checkout("20_KR_OFF").checkout(regular).total, 80.0)

    val fairCheckout = Checkout("HAPPY_HOUR_OR_CUSTOMER_PRICE")
    verify("stretch: best of: student gets happy hour (70 kr)", fairCheckout.checkout(student).total, 70.0)
    verify("stretch: best of: employee keeps 50 %", fairCheckout.checkout(employee).total, 50.0)

    // The pain, made visible: this compiles fine.
    val typo = Checkout("HAPPYHOUR")
    val result = runCatching { typo.checkout(regular) }
    println("\nCheckout(\"HAPPYHOUR\") compiles, then at the till: ${result.exceptionOrNull()?.message}")
}
