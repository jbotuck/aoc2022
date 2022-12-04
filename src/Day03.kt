fun main() {
    val lines = readInput("Day03")
    println("part 1 ${lines.sumOf { part1(it) }}")
    println("part 2 ${lines.chunked(3).sumOf { part2(it) }}")
}

private fun Char.priority() = if (isUpperCase()) code % 'A'.code + 27 else code % 'a'.code + 1

private fun part1(line: String): Int {
    val compartmentSize = line.length / 2
    return line.substring(0, compartmentSize).toSet()
        .intersect(line.substring(compartmentSize).toSet())
        .first().priority()
}

fun part2(group: List<String>) = group.map { it.toSet() }.reduce { a, b -> a.intersect(b) }.first().priority()


