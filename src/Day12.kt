fun main() {
    val lines = readInput("Day12")
    solve('S', lines)
    solve('a', lines)
}

fun solve(target: Char, lines: List<String>) {
    val distances = Array(lines.size) { Array(lines.first().length) { Int.MAX_VALUE } }
    val visited = mutableSetOf<Pair<Int, Int>>()
    val start = lines.withIndex().firstNotNullOf { indexedLine ->
        indexedLine.value.indexOfFirst { it == 'E' }.takeUnless { it == -1 }?.let { indexedLine.index to it }
    }
    distances.set(start, 0)
    val toVisit = mutableListOf(start)
    while (toVisit.isNotEmpty()) {
        val visiting = toVisit.removeClosest(distances)
        if (visiting in visited) continue
        visited.add(visiting)
        if (lines.getOrNull(visiting) == target) {
            println(distances.get(visiting))
            return
        }
        val distanceOfNeighbor = distances.get(visiting).inc()
        for (index in lines.neighborsOf(visiting).filter { it !in visited }) {
            toVisit.add(index)
            if (distanceOfNeighbor < distances.get(index)) distances.set(index, distanceOfNeighbor)
        }
    }
}

private fun MutableList<Pair<Int, Int>>.removeClosest(
    distances: Array<Array<Int>>
): Pair<Int, Int> = removeAt(indexOfClosest(distances))

private fun List<Pair<Int, Int>>.indexOfClosest(distances: Array<Array<Int>>) =
    withIndex().minBy { distances.get(it.value) }.index

fun Array<Array<Int>>.set(index: Pair<Int, Int>, value: Int) {
    get(index.first)[index.second] = value
}

fun Array<Array<Int>>.get(index: Pair<Int, Int>) = get(index.first)[index.second]


private fun List<String>.neighborsOf(index: Pair<Int, Int>) = listOf(
    above(index),
    below(index),
    leftOf(index),
    rightOf(index)
).filter { neighbor ->
    getOrNull(neighbor)?.height()?.let { neighborHeight ->
        neighborHeight >= getOrNull(index)!!.height().dec()
    } ?: false
}

fun above(index: Pair<Int, Int>) = (index.first.dec() to index.second)
fun below(index: Pair<Int, Int>) = (index.first.inc() to index.second)

fun leftOf(index: Pair<Int, Int>) = (index.first to index.second.dec())

fun rightOf(index: Pair<Int, Int>) = (index.first to index.second.inc())

private fun Char.height() = when (this) {
    'S' -> 'a'.code
    'E' -> 'z'.code
    else -> code
}

private fun List<String>.getOrNull(index: Pair<Int, Int>) = getOrNull(index.first)?.getOrNull(index.second)
