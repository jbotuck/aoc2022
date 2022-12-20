import kotlin.math.max
import kotlin.math.min

fun main() {
    val valves = readInput("Day16")
        .map { Valve.of(it) }
        .associateBy { it.name }
    val valuableValves = valves.values.filter { it.flowRate > 0 }.toSet()
    val costs = mutableMapOf<Pair<String, String>, Int>()
    val solutions = ArrayDeque<Solution>().apply { addLast(Solution(listOf("AA"))) }
    var maxRelief = 0
    while (solutions.isNotEmpty()) {
        val solution = solutions.removeFirst()
        var candidateDestinations = valuableValves.filter { it.name !in solution.visited }
        for (destination in candidateDestinations) {
            costs.computeIfAbsent(solution.lastVisited() to destination.name) {
                valves.calculateCost(
                    it.first,
                    it.second
                )
            }
        }
        candidateDestinations =
            candidateDestinations.filter { costs[solution.lastVisited() to it.name]!! < solution.timeRemaining() }
        if (candidateDestinations.isEmpty()) {
            maxRelief = max(maxRelief, solution.totalRelief(costs, valves))
            continue
        }
        solutions.addAll(
            candidateDestinations.map { solution.with(it.name, costs[solution.lastVisited() to it.name]!!) }
        )
    }
    println(maxRelief)
}

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

class Valve(val name: String, val flowRate: Int, val neighbors: List<String>) {
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

data class Solution(val visited: List<String>, val costIncurred: Int = 0) {
    fun lastVisited() = visited.last()
    fun timeRemaining(timeAllowed: Int = 30) = timeAllowed - costIncurred
    fun with(name: String, newCost: Int) = Solution(visited + name, costIncurred + newCost)
    fun totalRelief(costs: Map<Pair<String, String>, Int>, valves: Map<String, Valve>, timeAllowed: Int = 30): Int {
        var elapsedTime = 0
        var ret = 0
        for (visit in visited.windowed(2)) {
            elapsedTime += costs[visit.first() to visit.last()]!!
            ret += valves[visit.last()]!!.totalRelief(timeAllowed - elapsedTime)
        }
        return ret
    }
}