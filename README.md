# Kotlin Kaffe – a design patterns workshop

Kotlin Kaffe is a small café. In this workshop you follow one order from start to finish, and each step uses one design pattern:

| Step | What happens | Pattern | File |
| --- | --- | --- | --- |
| 1 | Build the drink | Decorator | `tasks/src/main/kotlin/kaffe/drink/CustomDrink.kt` |
| 2 | Price the order | Strategy | `tasks/src/main/kotlin/kaffe/checkout/Checkout.kt` |
| 3 | Place the order | Observer | `tasks/src/main/kotlin/kaffe/order/OrderService.kt` |

**The steps are independent.** You can start with any of them, and you never need the solution to one step to do another. All steps share a small model in `kaffe/model/Model.kt`. Read it, but don't change it.

## Getting started

1. Open the project folder in IntelliJ IDEA and let Gradle sync.
2. Open the file for your step and read the comment at the top.
3. Click the green arrow next to `fun main()` to run it.
4. Change the design. Then uncomment the checks under `Checks for your new design` and run again until every line says `OK`.


## Step 1 – Build the drink (Decorator)

Today a drink is one class with a `Boolean` per add-on. Customers now want a **double shot**, and **oat milk** is a new add-on. Change the design so that a new add-on never requires changing an existing class, and add-ons can be combined freely, even the same one twice.

<details><summary>Hint 1</summary>

A double shot is "an espresso, with an extra shot, with an extra shot". What if each add-on was its own object that knew about the drink *underneath* it?
</details>

<details><summary>Hint 2</summary>

What should the type of "espresso with milk" be? If it is also a `Drink`, what can you wrap around it?
</details>

<details><summary>Hint 3</summary>

Try writing `class Milk(private val drink: Drink) : Drink`. What should `cost()` return?
</details>

**Stretch:** Add `Takeaway`, which only changes the description (`" (takeaway)"`), not the price. Can you use Kotlin's class delegation (`: Drink by drink`) so you only override one method?

## Step 2 – Price the order (Strategy)

Checkout has discounts for students and employees. Marketing now wants **happy hour (30 % off for everyone)**, and a new campaign every week. Change the design so you can create one checkout with normal pricing and one with happy hour, and a new campaign never requires changing `Checkout`.

<details><summary>Hint 1</summary>

Which part of `Checkout` changes every week, and which part stays the same?
</details>

<details><summary>Hint 2</summary>

What if `Checkout` was *given* the rule for calculating the price, instead of knowing it?
</details>

<details><summary>Hint 3</summary>

Try an interface with one method, `fun price(order: Order): Double`, and pass it to `Checkout`'s constructor.
</details>

**Stretch:** Make your interface a `fun interface` and write a campaign as a lambda. Then write `bestOf(ruleA, ruleB)`, which gives the customer whichever price is lowest.

## Step 3 – Place the order (Observer)

When an order is placed, the barista screen, the loyalty program and the receipt email all react. Every new reaction means editing `OrderService`. The stats team now wants to **count drinks sold**. Change the design so they can add a `SalesStats` without changing `OrderService`, and `OrderService` doesn't know who is reacting.

<details><summary>Hint 1</summary>

Today `OrderService` calls each reaction by name. What if it just kept a list of "someone who wants to know"?
</details>

<details><summary>Hint 2</summary>

What do the barista screen, the loyalty program and the receipt email have in common, from `OrderService`'s point of view?
</details>

<details><summary>Hint 3</summary>

Try an interface `OrderListener` with `fun orderPlaced(order: PricedOrder)`, and a `subscribe(listener)` method on `OrderService`.
</details>

**Stretch:** Add `unsubscribe`, so a customer can turn off receipt emails. Then try replacing your list of listeners with a `MutableSharedFlow<PricedOrder>` from kotlinx.coroutines (already added as a dependency).

---
