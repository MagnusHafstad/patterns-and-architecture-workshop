package kaffe.solution.checkout

import kaffe.model.Customer
import kaffe.model.CustomerType
import kaffe.model.Order
import kaffe.model.PricedOrder
import kaffe.model.SimpleDrink
import kaffe.model.verify

// STEP 2 – reference solution (Strategy)
//
// The part that VARIES (how to calculate the price) is pulled out into its own type, PricingRule.
// Checkout gets a PricingRule when it is created and never needs to change again.

fun interface PricingRule {
    fun price(order: Order): Double
}

class Checkout(private val pricing: PricingRule) {
    fun checkout(order: Order): PricedOrder = PricedOrder(order, pricing.price(order))
}

// The old rules, moved out of Checkout unchanged.
object CustomerTypePricing : PricingRule {
    override fun price(order: Order): Double = when (order.customer.type) {
        CustomerType.REGULAR -> order.subtotal
        CustomerType.STUDENT -> order.subtotal * 0.8
        CustomerType.EMPLOYEE -> order.subtotal * 0.5
    }
}

// The new campaign: one new class, Checkout untouched.
object HappyHour : PricingRule {
    override fun price(order: Order): Double = order.subtotal * 0.7
}

// Stretch goal 1: because PricingRule is a `fun interface`, a lambda is a strategy too.
val twentyKronerOff = PricingRule { order -> maxOf(order.subtotal - 20.0, 0.0) }

// Stretch goal 2: combine strategies. The customer always gets the cheapest price.
fun bestOf(vararg rules: PricingRule) = PricingRule { order -> rules.minOf { it.price(order) } }

fun main() {
    val drinks = listOf(SimpleDrink("Latte", 55.0), SimpleDrink("Cappuccino", 45.0))
    val regular = Order(1, Customer("Ola", CustomerType.REGULAR), drinks)
    val student = Order(2, Customer("Kari", CustomerType.STUDENT), drinks)
    val employee = Order(3, Customer("Nora", CustomerType.EMPLOYEE), drinks)

    val normalCheckout = Checkout(CustomerTypePricing)
    verify("normal: regular pays 100 kr", normalCheckout.checkout(regular).total, 100.0)
    verify("normal: student pays 80 kr", normalCheckout.checkout(student).total, 80.0)
    verify("normal: employee pays 50 kr", normalCheckout.checkout(employee).total, 50.0)

    val happyHourCheckout = Checkout(HappyHour)
    verify("happy hour: regular pays 70 kr", happyHourCheckout.checkout(regular).total, 70.0)
    verify("happy hour: student pays 70 kr", happyHourCheckout.checkout(student).total, 70.0)

    verify("stretch: lambda strategy, 20 kr off", Checkout(twentyKronerOff).checkout(regular).total, 80.0)

    val fairCheckout = Checkout(bestOf(CustomerTypePricing, HappyHour))
    verify("stretch: best of: student gets happy hour (70 kr)", fairCheckout.checkout(student).total, 70.0)
    verify("stretch: best of: employee keeps 50 %", fairCheckout.checkout(employee).total, 50.0)
}
