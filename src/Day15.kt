import kotlin.math.abs
import kotlin.math.max

fun main() {
    val sensors = readInput("Day15")
        .map { Sensor.of(it) }
    //part1
    val row = 2000000
    sensors
        .map { it.ruleOutForRow(row) }
        .run {
            (minOf { it.first }..maxOf { it.last })
                .count { x ->
                    any { x in it }
                }
        }.let { println(it) }
    //part2
    val beacons = sensors.map { it.beacon }.toSet()
    val space = 4_000_000
    (0..space).firstNotNullOf { y ->
        val ruleOuts = sensors
            .asSequence()
            .map { it.ruleOutForRow(y) }
            .filter { !it.isEmpty() }
            .filter { it.last >= 0 }
            .filter { it.first <= space }
            .sortedBy { it.first }
            .fold(mutableListOf<IntRange>()) { acc, intRange ->
                val lastRange = acc.lastOrNull()
                when {
                    lastRange == null -> mutableListOf(intRange)
                    intRange.first in lastRange -> acc.apply {
                        set(
                            lastIndex,
                            lastRange.first..max(lastRange.last, intRange.last)
                        )
                    }

                    else -> acc.apply {
                        add(intRange)
                    }
                }
            }
        var lastVisited = -1
        ruleOuts.firstNotNullOfOrNull { ruleOut ->
            (lastVisited.inc() until ruleOut.first())
                .firstOrNull { it to y !in beacons }
                .also { lastVisited = ruleOut.last }
        }?.let {
            it to y
        }
    }.let {
        println(it)
        println(it.first.toLong() * 4000000 + it.second)
    }
}

data class Sensor(val location: Pair<Int, Int>, val beacon: Pair<Int, Int>) {
    fun ruleOutForRow(y: Int): IntRange {
        val distanceToBeacon = distanceToBeacon
        val remainingDistance = distanceToBeacon - abs(y - location.second)
        return when {
            beacon.second == y -> {
                if (beacon.first < location.first)
                    beacon.first.inc()..location.first + remainingDistance
                else
                    location.first - remainingDistance until beacon.first
            }

            remainingDistance < 0 -> IntRange.EMPTY
            remainingDistance == 0 -> location.first..location.first
            else -> ((location.first - remainingDistance)..(location.first + remainingDistance))
        }

    }

    private val distanceToBeacon = abs(location.first - beacon.first) + abs(location.second - beacon.second)

    companion object {
        fun of(input: String) = input
            .split(':')
            .map { s -> s.filter { it.isDigit() || it in listOf('-', ',') } }
            .map { coordinates -> coordinates.split(',').map { it.toInt() } }
            .map { it.first() to it.last() }
            .let { Sensor(it.first(), it.last()) }
    }
}