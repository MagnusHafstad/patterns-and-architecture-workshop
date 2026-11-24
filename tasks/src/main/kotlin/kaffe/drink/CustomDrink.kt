package kaffe.drink

import kaffe.model.Drink
import kaffe.model.verify

// ─────────────────────────────────────────────────────────────
//  STEP 1 – BUILD THE DRINK                    Pattern: Decorator
// ─────────────────────────────────────────────────────────────
//
//  Today every drink is ONE class with a true/false flag per add-on.
//  It works, but the café has new wishes:
//
//    1. Customers want a double shot: "Espresso + extra shot + extra shot" (55 kr).
//       A Boolean flag can only say yes or no, not "twice".
//    2. Oat milk (+10 kr) is a new add-on. Next month there will be another one.
//
//  Your task: change the design so that
//    - a new add-on can be added WITHOUT changing any existing class
//    - add-ons can be combined in any order, and the same one used more than once
//
//  The checks at the bottom of main() tell you when you are done.
//  See README.md for hints and a stretch goal.

class CustomDrink(
    private val base: String, // "Espresso" or "Filter coffee"
    private val milk: Boolean = false,
    private val extraShot: Boolean = false,
    private val vanillaSyrup: Boolean = false,
) : Drink {

    override fun cost(): Double {
        var cost = when (base) {
            "Espresso" -> 35.0
            "Filter coffee" -> 30.0
            else -> error("Unknown drink: $base")
        }
        if (milk) cost += 8.0
        if (extraShot) cost += 10.0
        if (vanillaSyrup) cost += 6.0
        return cost
    }

    override fun description(): String {
        var text = base
        if (milk) text += " + milk"
        if (extraShot) text += " + extra shot"
        if (vanillaSyrup) text += " + vanilla syrup"
        return text
    }
}

fun main() {
    val today = CustomDrink("Espresso", milk = true, extraShot = true)
    println("${today.description()}: ${today.cost()} kr")

    println("Checks for what already works:")
    verify("espresso + milk + extra shot costs 53 kr", today.cost(), 53.0)
    verify("description lists the add-ons", today.description(), "Espresso + milk + extra shot")

    println("Checks for your new design:")
    // TODO: build these drinks with your new design, then uncomment the lines below.

    // val sameAsToday: Drink = ...   // Espresso + milk + extra shot
    // verify("same drink, new design: 53 kr", sameAsToday.cost(), 53.0)
    // verify("same description", sameAsToday.description(), "Espresso + milk + extra shot")

    // val doubleShot: Drink = ...    // Espresso + extra shot + extra shot
    // verify("double shot costs 55 kr", doubleShot.cost(), 55.0)
    // verify("double shot description", doubleShot.description(), "Espresso + extra shot + extra shot")

    // val oatFilter: Drink = ...     // Filter coffee + oat milk
    // verify("filter coffee with oat milk costs 40 kr", oatFilter.cost(), 40.0)
    // verify("oat milk description", oatFilter.description(), "Filter coffee + oat milk")
}
