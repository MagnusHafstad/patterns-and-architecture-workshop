package kaffe.solution.drink

import kaffe.model.Drink
import kaffe.model.verify

// STEP 1 – reference solution (Decorator)
//
// The base drinks are plain classes.
// Every add-on is a Drink that WRAPS another Drink and adds a little on top.
// Because an add-on is itself a Drink, add-ons can wrap each other in any order, any number of times.

class Espresso : Drink {
    override fun description() = "Espresso"
    override fun cost() = 35.0
}

class FilterCoffee : Drink {
    override fun description() = "Filter coffee"
    override fun cost() = 30.0
}

class Milk(private val drink: Drink) : Drink {
    override fun description() = drink.description() + " + milk"
    override fun cost() = drink.cost() + 8.0
}

class ExtraShot(private val drink: Drink) : Drink {
    override fun description() = drink.description() + " + extra shot"
    override fun cost() = drink.cost() + 10.0
}

class VanillaSyrup(private val drink: Drink) : Drink {
    override fun description() = drink.description() + " + vanilla syrup"
    override fun cost() = drink.cost() + 6.0
}

// The new add-on: one new class, nothing else changed.
class OatMilk(private val drink: Drink) : Drink {
    override fun description() = drink.description() + " + oat milk"
    override fun cost() = drink.cost() + 10.0
}

// Stretch goal: class delegation with `by`.
// Takeaway only changes the description. `: Drink by drink` forwards every method
// we do not override (here: cost()) to the wrapped drink automatically.
class Takeaway(private val drink: Drink) : Drink by drink {
    override fun description() = drink.description() + " (takeaway)"
}

fun main() {
    val sameAsToday: Drink = ExtraShot(Milk(Espresso()))
    verify("same drink, new design: 53 kr", sameAsToday.cost(), 53.0)
    verify("same description", sameAsToday.description(), "Espresso + milk + extra shot")

    val doubleShot: Drink = ExtraShot(ExtraShot(Espresso()))
    verify("double shot costs 55 kr", doubleShot.cost(), 55.0)
    verify("double shot description", doubleShot.description(), "Espresso + extra shot + extra shot")

    val oatFilter: Drink = OatMilk(FilterCoffee())
    verify("filter coffee with oat milk costs 40 kr", oatFilter.cost(), 40.0)
    verify("oat milk description", oatFilter.description(), "Filter coffee + oat milk")

    val toGo: Drink = Takeaway(doubleShot)
    verify("stretch: takeaway keeps the price", toGo.cost(), 55.0)
    verify("stretch: takeaway description", toGo.description(), "Espresso + extra shot + extra shot (takeaway)")
}
