fun main() {
    val lines = readInput("Day01")
    val top3 = lines
        .fold(mutableListOf(mutableListOf<Int>())) { acc, s ->
            acc.apply {
                s.toIntOrNull()
                    ?.let { last().add(it) }
                    ?: add(mutableListOf())
            }
        }.map { it.sum() }
        .sorted()
        .takeLast(3)

    println("part 1")
    println(top3.last())
    println("part 2")
    println(top3.sum())
}
