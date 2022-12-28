import kotlin.math.max

@Suppress("UnnecessaryVariable", "UNUSED_VARIABLE")
fun main() {
    val board17 = TetrisBoard(readInput("Day17").first())
    val boardSample = TetrisBoard(">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>")
    val board = board17
    board.play()
    println(board.height())
}

private data class Shape(val points: Set<Pair<Int, Long>>) {
    fun addY(y: Long) = Shape(points.map { it.first to it.second.plus(y) }.toSet())
    fun maxHeight() = points.maxOf { it.second }
    fun addX(x: Int) = Shape(points.map { it.first.plus(x) to it.second }.toSet())
}

private val shapeBlueprints = listOf(
    Shape((2..5).map { it to 0L }.toSet()),
    Shape(setOf(3 to 2L, 3 to 0L) + (2..4).map { it to 1L }),
    Shape((2..4).map { it to 0L }.toSet() + setOf(4 to 1, 4 to 2)),
    Shape((0L..3L).map { 2 to it }.toSet()),
    Shape((2..3).flatMap { x -> (0L..1L).map { y -> x to y } }.toSet())
)

private class TetrisBoard(val input: String) {
    private var nextInputIndex = 0
    private var rocks = mutableSetOf<Pair<Int, Long>>()
    private var shapesGenerated = 0
    private var indexOfHighestStone = -1L

    private var offset = 0L
    private var shape = generateShape()

    fun height() = offset + indexOfHighestStone.inc()
    private fun generateShape() = shapeBlueprints[shapesGenerated++ % shapeBlueprints.size]
        .addY(indexOfHighestStone + 4)


    fun play(numberOfRocksToPlay: Long = 2022) {
        while (shapesGenerated <= numberOfRocksToPlay) {
            playUntilLanding()
        }
    }

    private fun playUntilLanding() {
        while (true) {
            applyInput(input[nextInputIndex++])
            if (nextInputIndex == input.length) nextInputIndex = 0
            if (!moveDown()) break
        }
        setInStone(shape)
        shape = generateShape()
    }

    private fun moveDown() = shape.addY(-1).takeIf { it.isValid() }?.also { shape = it }?.let { true } ?: false

    private fun applyInput(next: Char) = shape
        .addX(if (next == '>') 1 else -1)
        .takeIf { it.isValid() }
        ?.let { shape = it }


    private fun setInStone(shape: Shape) {
        rocks.addAll(shape.points)
        indexOfHighestStone = max(indexOfHighestStone, shape.maxHeight())
        normalize()
    }

    private fun normalize() {
        val unreachableRows = unreachableRows()
        if (unreachableRows > 0) {
            offset += unreachableRows
            indexOfHighestStone -= unreachableRows
            rocks = rocks.map { it.first to it.second.minus(unreachableRows) }.filter { it.second >= 0 }.toMutableSet()
        }
    }

    private fun unreachableRows(): Long {
        val toVisit = ArrayDeque<Pair<Int, Long>>().apply { addLast(0 to indexOfHighestStone.inc()) }
        val visited = mutableSetOf<Pair<Int, Long>>()
        while (toVisit.isNotEmpty()) {
            val visiting = toVisit.removeFirst()
            visiting
                .neighbors()
                .asSequence()
                .filter { it !in visited }
                .filter { it !in rocks }
                .filter { it.first >= 0 && it.second >= 0L }
                .filter { it.first <= 6 }
                .filter { it.second <= indexOfHighestStone.inc() }
                .let { toVisit.addAll(it) }
            visited.add(visiting)
        }
        return visited.minOf { it.second }
    }

    private fun Shape.isValid(): Boolean {
        return points.all {
            it.first in 0..6 && it.second >= 0 && it !in rocks
        }
    }
}

private fun Pair<Int, Long>.neighbors() =
    listOf(first to second.inc(), first to second.dec(), first.inc() to second, first.dec() to second)

