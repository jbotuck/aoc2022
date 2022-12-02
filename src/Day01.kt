fun main() {
    val lines = readInput("Day01")

    val top3 = mutableListOf(0).apply {
        for (line in lines) {
            line.toIntOrNull()?.let { set(lastIndex, last() + it) } ?: add(0)
        }
    }.sorted().takeLast(3)

    println("part 1")
    println(top3.last())
    println("part 2")
    println(top3.sum())
}
