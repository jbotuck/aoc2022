import kotlin.math.max
import kotlin.math.min

fun main() {
    val rockStructures = readInput("Day14").map { line ->
        line.split(" -> ").map { pair ->
            pair.split(",")
                .map { it.toInt() }
                .let { it.first() to it.last() }
        }
    }

    val maxY = rockStructures.maxOf { structure ->
        structure.maxOf { it.second }
    }
    run {
        val cave = Cave2(maxY.inc(), false)
        cave.build(rockStructures)
        cave.print()
        cave.fill()
        println("cave filled")
        cave.print()
        println(cave.countTheSand())
    }
    val cave = Cave2(maxY + 2, true)
    cave.build(rockStructures)
    cave.print()
    cave.fill()
    println("cave filled")
    cave.print()
    println(cave.countTheSand())

}

private class Cave2(val floorIndex: Int, val hasFloor: Boolean) {
    private val rocks = mutableSetOf<Pair<Int, Int>>()
    private val sand = mutableSetOf<Pair<Int, Int>>()
    fun fill() {
        while (true) {
            if (!generateSandParticleAndCheckIsRendered()) break
        }
    }

    private fun Pair<Int, Int>.isBlocked() = this in sand || this in rocks || (hasFloor && second == floorIndex)

    private fun generateSandParticleAndCheckIsRendered(): Boolean {
        var particle = 500 to 0
        if (particle.isBlocked()) return false
        while (true) {
            val next = sequenceOf(
                particle.copy(second = particle.second.inc()),
                Pair(particle.first.dec(), particle.second.inc()),
                Pair(particle.first.inc(), particle.second.inc())
            ).firstOrNull { !it.isBlocked() }
            if (next == null) {
                sand.add(particle)
                return true
            }
            if (next.second == floorIndex) return false
            particle = next
        }
    }

    fun buildSingle(structure: List<Pair<Int, Int>>) {
        for (segment in structure.windowed(2)) {
            @Suppress("NAME_SHADOWING")
            val segment = segment.sortedWith { a, b ->
                a.first.compareTo(b.first).takeIf { it != 0 } ?: a.second.compareTo(b.second)
            }
            rocks.addAll(
                (segment.first().first..segment.last().first)
                    .takeIf { it.first != it.last() }
                    ?.map { it to segment.first().second }
                    ?: (segment.first().second..segment.last().second)
                        .map { segment.first().first to it }
            )
        }
    }

    fun countTheSand() = sand.size
    fun print() {
        val minX = min(rocks.minOf { it.first }, sand.minOfOrNull { it.first } ?: Int.MAX_VALUE)
        val maxX = max(rocks.maxOf { it.first }, sand.maxOfOrNull { it.first } ?: Int.MIN_VALUE)

        println()
        for (y in 0..floorIndex) {
            println()
            for (x in minX..maxX)
                print((x to y).let {
                    when (it) {
                        in rocks -> '#'
                        in sand -> 'o'
                        else -> '.'
                    }
                })
        }
        println()
    }

    fun build(structures: List<List<Pair<Int, Int>>>) {
        for (structure in structures) buildSingle(structure)
    }
}

