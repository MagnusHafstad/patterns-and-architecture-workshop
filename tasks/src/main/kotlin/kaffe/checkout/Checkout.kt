package kaffe.checkout

import kaffe.model.Customer
import kaffe.model.CustomerType
import kaffe.model.Order
import kaffe.model.PricedOrder
import kaffe.model.SimpleDrink
import kaffe.model.verify

// ─────────────────────────────────────────────────────────────
//  STEP 2 – PRICE THE ORDER                     Pattern: Strategy
// ─────────────────────────────────────────────────────────────
//
//  Checkout gives students 20 % off and employees 50 % off.
//  Now marketing wants to run campaigns, and they change every week:
//
//    1. This week: HAPPY HOUR – everyone gets 30 % off.
//    2. Next week: something new. Nobody knows what yet.
//
//  The café wants to choose which pricing rule is active when the till starts,
//  e.g. normal pricing in the morning and happy hour in the afternoon.
//
//  Your task: change the design so that
//    - you can create one Checkout with normal pricing and one with happy hour pricing
//    - a new campaign can be added WITHOUT changing the Checkout class
//
//  The checks at the bottom of main() tell you when you are done.
//  See README.md for hints and a stretch goal.

class Checkout {
    fun checkout(order: Order): PricedOrder {
        val total = when (order.customer.type) {
            CustomerType.REGULAR -> order.subtotal
            CustomerType.STUDENT -> order.subtotal * 0.8
            CustomerType.EMPLOYEE -> order.subtotal * 0.5
        }
        return PricedOrder(order, total)
    }
}

fun main() {
    // Every order below costs 100 kr before discount.
    val drinks = listOf(SimpleDrink("Latte", 55.0), SimpleDrink("Cappuccino", 45.0))
    val regular = Order(1, Customer("Ola", CustomerType.REGULAR), drinks)
    val student = Order(2, Customer("Kari", CustomerType.STUDENT), drinks)
    val employee = Order(3, Customer("Nora", CustomerType.EMPLOYEE), drinks)

    println("Checks for what already works:")
    val checkout = Checkout()
    verify("regular pays 100 kr", checkout.checkout(regular).total, 100.0)
    verify("student pays 80 kr", checkout.checkout(student).total, 80.0)
    verify("employee pays 50 kr", checkout.checkout(employee).total, 50.0)

    println("Checks for your new design:")
    // TODO: create the two checkouts with your new design, then uncomment the lines below.

    // val normalCheckout = ...      // same rules as today
    // verify("normal: regular pays 100 kr", normalCheckout.checkout(regular).total, 100.0)
    // verify("normal: student pays 80 kr", normalCheckout.checkout(student).total, 80.0)
    // verify("normal: employee pays 50 kr", normalCheckout.checkout(employee).total, 50.0)

    // val happyHourCheckout = ...   // 30 % off for everyone
    // verify("happy hour: regular pays 70 kr", happyHourCheckout.checkout(regular).total, 70.0)
    // verify("happy hour: student pays 70 kr", happyHourCheckout.checkout(student).total, 70.0)
}
