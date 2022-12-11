import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val lines = readInput("Day09")
    var rope = Rope()
    for (line in lines) {
        rope.execute(line)
    }
    println(rope.countOfSpotsTailVisited())
    rope = Rope(10)
    for (line in lines) {
        rope.execute(line)
    }
    println(rope.countOfSpotsTailVisited())
}

private data class Point(val x: Int = 0, val y: Int = 0) {
    fun follow(other: Point): Point {
        val xDiff = other.x - x
        val yDiff = other.y - y
        return if (abs(xDiff) > 1 || abs(yDiff) > 1) {
            Point(
                x + sign(xDiff.toDouble()).toInt(),
                y + sign(yDiff.toDouble()).toInt()
            )
        } else this
    }
}

private class Rope(knotCount: Int = 2) {
    val knots = Array(knotCount) { Point() }
    val visited = mutableSetOf(knots.last())
    fun countOfSpotsTailVisited() = visited.size
    fun execute(line: String) {
        val (direction, distance) = line.split(" ")
        val move: Point.() -> Point = when (direction) {
            "U" -> {
                { copy(y = y.inc()) }
            }

            "D" -> {
                { copy(y = y.dec()) }
            }

            "L" -> {
                { copy(x = x.dec()) }
            }

            "R" -> {
                { copy(x = x.inc()) }
            }

            else -> throw IllegalArgumentException()
        }
        repeat(distance.toInt()) {
            knots[0] = knots[0].move()
            for (i in 1..knots.lastIndex) {
                knots[i] = knots[i].follow(knots[i.dec()])
            }
            visited.add(knots.last())
        }
    }
}