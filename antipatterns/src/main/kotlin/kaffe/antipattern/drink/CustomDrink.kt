package kaffe.antipattern.drink

import kaffe.model.Drink
import kaffe.model.verify

// STEP 1 – antipattern solution (no Decorator)
//
// The "just make the checks pass" approach: keep the one big class and add more flags.
//   - `extraShot: Boolean` became `extraShots: Int`, so "twice" fits.
//   - Oat milk became one more Boolean.
//   - Takeaway (stretch) became one more Boolean.
//
// It works, and every check passes. Compare it with solutions/.../drink/Drinks.kt:
//   - Every new add-on means editing this class in THREE places: the constructor, cost() and description().
//   - The order of add-ons is hard-coded. You can't make "Espresso + extra shot + milk", only "+ milk + extra shot".
//   - Double vanilla? Change another Boolean to an Int and update every caller.
//   - Nothing stops nonsense: milk = true AND oatMilk = true, or extraShots = -3.
//   - The base drink is a String, so "Espreso" compiles fine and crashes at the till.
//   - Takeaway has nothing to do with the recipe, but it still lives in the same class.

class CustomDrink(
    private val base: String, // "Espresso" or "Filter coffee"
    private val milk: Boolean = false,
    private val extraShots: Int = 0,          // CHANGED: was `extraShot: Boolean`. Broke every existing caller.
    private val vanillaSyrup: Boolean = false,
    private val oatMilk: Boolean = false,     // NEW: and next month another one goes here...
    private val takeaway: Boolean = false,    // NEW (stretch): ...and here.
) : Drink {

    override fun cost(): Double {
        var cost = when (base) {
            "Espresso" -> 35.0
            "Filter coffee" -> 30.0
            else -> error("Unknown drink: $base")
        }
        if (milk) cost += 8.0
        cost += extraShots * 10.0
        if (vanillaSyrup) cost += 6.0
        if (oatMilk) cost += 10.0
        // takeaway is free, but you have to remember NOT to add anything here
        return cost
    }

    override fun description(): String {
        var text = base
        if (milk) text += " + milk"
        repeat(extraShots) { text += " + extra shot" }
        if (vanillaSyrup) text += " + vanilla syrup"
        if (oatMilk) text += " + oat milk"
        if (takeaway) text += " (takeaway)"
        return text
    }
}

fun main() {
    val sameAsToday: Drink = CustomDrink("Espresso", milk = true, extraShots = 1)
    verify("same drink, new design: 53 kr", sameAsToday.cost(), 53.0)
    verify("same description", sameAsToday.description(), "Espresso + milk + extra shot")

    val doubleShot: Drink = CustomDrink("Espresso", extraShots = 2)
    verify("double shot costs 55 kr", doubleShot.cost(), 55.0)
    verify("double shot description", doubleShot.description(), "Espresso + extra shot + extra shot")

    val oatFilter: Drink = CustomDrink("Filter coffee", oatMilk = true)
    verify("filter coffee with oat milk costs 40 kr", oatFilter.cost(), 40.0)
    verify("oat milk description", oatFilter.description(), "Filter coffee + oat milk")

    // Stretch: we can't wrap `doubleShot`, so we rebuild the whole drink with one more flag.
    val toGo: Drink = CustomDrink("Espresso", extraShots = 2, takeaway = true)
    verify("stretch: takeaway keeps the price", toGo.cost(), 55.0)
    verify("stretch: takeaway description", toGo.description(), "Espresso + extra shot + extra shot (takeaway)")

    // The pain, made visible:
    val nonsense: Drink = CustomDrink("Espresso", milk = true, oatMilk = true, extraShots = -3)
    println("\nThis compiles and runs: ${nonsense.description()} = ${nonsense.cost()} kr")
    println("(milk AND oat milk, and the -3 extra shots silently took 30 kr off the price)")
}
