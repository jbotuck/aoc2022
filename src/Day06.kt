fun main() {
    val lines = readInput("Day06")
    val part1 = solve(lines.first(), 4)
    val part2 = solve(lines.first(), 14)
    println("part 1 $part1")
    println("part 2 $part2")
}

private fun solve(line: String, windowSize: Int) = line
    .asSequence()
    .withIndex()
    .windowed(windowSize)
    .first { window ->
        window.map { it.value }.toSet().size == windowSize
    }.last()
    .index + 1
