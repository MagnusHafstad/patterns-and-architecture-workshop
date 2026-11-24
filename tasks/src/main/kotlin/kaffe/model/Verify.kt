package kaffe.model

import kotlin.math.abs

/**
 * Tiny helper for the checks at the bottom of each step.
 * Prints OK or FAIL instead of crashing, so you can see every result at once.
 */
fun verify(what: String, actual: Any?, expected: Any?) {
    val ok = if (actual is Double && expected is Double) {
        abs(actual - expected) < 0.001
    } else {
        actual == expected
    }
    if (ok) {
        println("  OK    $what")
    } else {
        println("  FAIL  $what: expected <$expected> but was <$actual>")
    }
}
