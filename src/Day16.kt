import kotlin.math.max
import kotlin.math.min

fun main() {
    val valves = readInput("Day16")
        .map { Valve.of(it) }
        .associateBy { it.name }
    val start = valves["AA"]!!
    val valvesWithNeighbors = valves.values
        .filter { it.name == "AA" || it.flowRate > 0 }
        .sortedBy { it.name }
        .let { valuableValves ->
            val distances = valuableValves.mapIndexed { index, origin ->
                (index.inc()..valuableValves.lastIndex)
                    .map {
                        (origin.name to valuableValves[it].name) to valves.calculateCost(
                            origin.name,
                            valuableValves[it].name
                        )
                    }
            }.flatten().toMap()
            valuableValves.associateWith { thisValve ->
                valuableValves
                    .filter { it != thisValve && it.name != "AA" }
                    .map { it to distances.getDistance(thisValve.name, it.name) }
                    .sortedBy { it.second }
            }
        }

    //part1
    run {
        val paths = getPaths(start, valvesWithNeighbors, 30)
        println(paths.values.max())
    }
    //part 2
    val paths = getPaths(start, valvesWithNeighbors, 26)
    paths.maxOf { me ->
        paths
            .filter { elephant -> elephant.key.all { it !in me.key } }
            .maxOfOrNull { elephant -> elephant.value + me.value }
            ?: 0
    }.let { println(it) }
}

private fun getPaths(
    start: Valve,
    valvesWithNeighbors: Map<Valve, List<Pair<Valve, Int>>>,
    timeAllowed: Int = 30
) = mutableMapOf<Set<String>, Int>().apply {
    val solutions = ArrayDeque<Solution>().apply { addLast(Solution(start, timeAllowed = timeAllowed)) }
    while (solutions.isNotEmpty()) {
        val solution = solutions.removeFirst()
        val key = solution.visited.map { it.name }.toSet()
        this[key] = max(solution.totalRelief, this[key] ?: 0)
        solution.nextSolutions(valvesWithNeighbors).takeUnless { it.isEmpty() }?.let { solutions.addAll(it) }
    }
}.toMap()

private fun Map<Pair<String, String>, Int>.getDistance(a: String, b: String) =
    get(listOf(a, b).sorted().run { first() to last() })!!


fun Map<String, Valve>.calculateCost(origin: String, destination: String): Int {
    val costs = mutableMapOf<String, Int>().apply { set(origin, 0) }
    val toVisit = mutableListOf(origin)
    val visited = mutableSetOf<String>()
    while (toVisit.isNotEmpty()) {
        val visiting = toVisit.removeAt(toVisit.withIndex().minBy { costs[it.value] ?: Int.MAX_VALUE }.index)
        if (visiting in visited) continue
        visited.add(visiting)
        if (visiting == destination) return costs[visiting]!!.inc() //Plus 1 to open the valve
        val distanceOfNeighbor = costs[visiting]!!.inc()
        for (neighbor in get(visiting)!!.neighbors) {
            toVisit.add(neighbor)
            costs[neighbor] = costs[neighbor]?.let { min(it, distanceOfNeighbor) } ?: distanceOfNeighbor
        }
    }
    throw IllegalStateException("oops")
}

data class Valve(val name: String, val flowRate: Int, val neighbors: List<String>) {
    fun totalRelief(timeOpen: Int) = flowRate * timeOpen

    companion object {
        fun of(line: String): Valve {
            val (valve, neighbors) = line.split(';')
            val (name, flowRate) = valve.substringAfter("Valve ").split(" has flow rate=")
            return Valve(
                name, flowRate.toInt(), neighbors
                    .substringAfter(" tunnel leads to valve ")
                    .substringAfter(" tunnels lead to valves ")
                    .split(", ")
            )
        }
    }
}

data class Solution(
    val visited: Set<Valve>,
    val lastVisited: Valve,
    val costIncurred: Int = 0,
    val timeAllowed: Int = 30,
    val totalRelief: Int = 0
) {

    constructor(valve: Valve, costIncurred: Int = 0, timeAllowed: Int = 30, totalRelief: Int = 0) : this(
        setOf(),
        valve,
        costIncurred,
        timeAllowed,
        totalRelief
    )

    private val timeRemaining = timeAllowed - costIncurred
    private fun with(valve: Valve, additionalCost: Int) = copy(
        visited = visited + valve,
        lastVisited = valve,
        costIncurred = costIncurred + additionalCost,
        totalRelief = totalRelief + valve.totalRelief(timeRemaining - additionalCost)
    )

    fun nextSolutions(valvesWithNeighbors: Map<Valve, List<Pair<Valve, Int>>>) = mutableListOf<Solution>().apply {
        for (destination in valvesWithNeighbors[lastVisited]!!) {
            if (destination.first in visited) continue
            if (destination.second > timeRemaining) break
            add(with(destination.first, destination.second))
        }
    }
}