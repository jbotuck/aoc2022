fun main() {
    val lines = readInput("Day01")
    val elfTotals = lines
        .fold(mutableListOf(mutableListOf<Int>())) { acc, s ->
            acc.apply {
                s.toIntOrNull()
                    ?.let { last().add(it) }
                    ?: add(mutableListOf())
            }
        }.map { it.sum() }
        .toList()

    val top3 = mutableListOf<Int>()
    for (elfTotal in elfTotals) {
        if (top3.size == 3 && elfTotal > top3.first()) top3.removeFirst()
        if (top3.size < 3) {
            top3.add(elfTotal)
            top3.sort()
        }
    }
    println("part 1")
    println(top3.last())
    println("part 2")
    println(top3.sum())
}
