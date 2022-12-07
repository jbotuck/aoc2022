import java.time.Duration
import java.time.Instant

fun main() {
    val started = Instant.now()
    val lines = readInput("Day02")
    println("part 1 ${lines.sumOf { score1(it) }}")
    println("part 2 ${lines.sumOf { score2(it) }}")
    println(Duration.between(started, Instant.now()).toMillis())
}

private val rules = setOf(0 to 2, 1 to 0, 2 to 1)//who beats who

private fun Pair<Int, Int>.score(opponent: Int) = 1 + first + when (opponent) {
    second -> 6
    first -> 3
    else -> 0
}

private fun score1(line: String) = rules
    .find { it.first == line.last().code % 'X'.code }!!
    .score(line.first().code % 'A'.code)

private fun score2(line: String): Int {
    val opponent = line.first().code % 'A'.code
    return when (line.last()) {
        'X' -> rules.find { it.first == opponent }!!.second.let { self -> rules.find { it.first == self } }
        'Y' -> rules.find { it.first == opponent }
        else -> rules.find { it.second == opponent }
    }!!.score(opponent)
}
