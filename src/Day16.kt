import Action.OPEN
import Action.TRAVEL_TO
import kotlin.math.max

fun main() {
    val valves = readInput("Day16")
        .map { Valve.of(it) }
        .associateBy { it.name }
    val solutions = ArrayDeque<Solution>().apply {
        addLast(Solution(listOf(TRAVEL_TO to valves["AA"]!!)))
    }
    var maxRelief = 0
    while (solutions.isNotEmpty()) {
        val solution = solutions.removeFirst()
        if (solution.isComplete()) {
            maxRelief = max(maxRelief, solution.totalRelief())
            continue
        }
        val lastAction = solution.actions.last()
        if (lastAction.first == TRAVEL_TO && lastAction.second.flowRate > 0 && OPEN to lastAction.second !in solution.actions) {
            solutions.add(solution.withAction(OPEN to lastAction.second))
        }
        val pointlessDestination = if (lastAction.first == TRAVEL_TO) {
            solution.actions.run { getOrNull(lastIndex.dec())?.second?.name }
        } else null
        lastAction.second.neighbors.filter { it != pointlessDestination }.mapNotNull { valves[it] }
            .map { solution.withAction(TRAVEL_TO to it) }
            .let { solutions.addAll(it) }
    }
    println(maxRelief)
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

enum class Action {
    OPEN,
    TRAVEL_TO
}

class Solution(val actions: List<Pair<Action, Valve>>) {
    fun isComplete() = actions.size == 31
    fun totalRelief() = actions
        .withIndex()
        .filter { it.value.first == OPEN }
        .sumOf { it.value.second.totalRelief(30 - it.index) }

    fun withAction(action: Pair<Action, Valve>) = Solution(actions + action)
}