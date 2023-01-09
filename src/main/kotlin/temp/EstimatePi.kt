package temp


fun randomInt (a: Int, b: Int): Int = Math.floor ((Math.random () * (b - a + 1)) + a).toInt ()

fun estimatePi (iterations: Int): Double {
    var hits = 0
    repeat (iterations) {
        val a = randomInt (1, 100)
        val b = randomInt (1 ,100)
        if (gcd (a, b) == 1) {
            hits ++
        }
    }
    val prob = hits.toDouble() / iterations
    return Math.sqrt (6.0 / prob)
}

fun main() {
    println (estimatePi (100_000_000))
}
