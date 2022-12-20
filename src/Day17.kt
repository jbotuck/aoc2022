import kotlin.math.max

fun main() {
    val board = TetrisBoard()
    board.play(myIterator(readInput("Day17").first()))
    //board.play(myIterator(">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"))
    println(board.indexOfHighestStone.inc())
}

private fun myIterator(s: String): Iterator<Char> {
    var iterator = s.iterator()
    return object : Iterator<Char> {
        override fun hasNext() = true

        override fun next() = if (iterator.hasNext()) iterator.next() else {
            iterator = s.iterator()
            iterator.next()
        }
    }
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

private class TetrisBoard {
    val rocks = mutableSetOf<Pair<Int, Long>>()
    var shapesGenerated = 0
    var indexOfHighestStone = -1L
    var shape = generateShape()

    private fun generateShape() = shapeBlueprints[shapesGenerated++ % shapeBlueprints.size]
        .addY(indexOfHighestStone + 4)


    fun play(input: Iterator<Char>, numberOfRocksToPlay: Long = 2022) {
        while (shapesGenerated <= numberOfRocksToPlay) {
            playUntilLanding(input)
        }
    }

    private fun playUntilLanding(input: Iterator<Char>) {
        while (true) {
            applyInput(input.next())
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
    }

    private fun Shape.isValid(): Boolean {
        return points.all {
            it.first in 0..6 && it.second >= 0 && it !in rocks
        }
    }
}

