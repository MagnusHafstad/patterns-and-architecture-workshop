package kaffe.model

// Shared model for all three steps.
// Read it, but you should not need to change anything in this file.

/** Something the café can sell. Has a price and a name for the receipt. */
interface Drink {
    fun description(): String
    fun cost(): Double
}

/**
 * A ready-made drink with a fixed price.
 * Lets step 2 and step 3 work on their own, without the solution to step 1.
 */
data class SimpleDrink(val name: String, val price: Double) : Drink {
    override fun description() = name
    override fun cost() = price
}

enum class CustomerType { REGULAR, STUDENT, EMPLOYEE }

data class Customer(val name: String, val type: CustomerType)

data class Order(val id: Int, val customer: Customer, val drinks: List<Drink>) {
    /** Sum of all drinks, before any discount. */
    val subtotal: Double get() = drinks.sumOf { it.cost() }
}

/** An order after checkout, with the final price the customer pays. */
data class PricedOrder(val order: Order, val total: Double)
