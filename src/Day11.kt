fun main() {//hardcodedMonkeys()implementation  intentionally hidden from gitHub as it is input specific
    var monkeys = hardCodedMonkeys()
    println(solve(monkeys, 20) { it / 3 })
    monkeys = hardCodedMonkeys()
    println(solve(monkeys, 10_000) { worryLevel ->
        worryLevel % monkeys.map { it.testDivisibilityBy }.reduce { a, b -> a * b }
    })
}

fun solve(monkeys: List<Monkey>, rounds: Int, reducer: (Long) -> Long): Long {
    repeat(rounds) {
        for (monkey in monkeys) {
            monkey.takeTurn(reducer) { monkeys[it] }
        }
    }
    return monkeys.map { it.numInspections }.sorted().takeLast(2).reduce { a, b -> a * b }
}

class Monkey(
    startingItems: List<Long>,
    private val operation: (Long) -> Long,
    val testDivisibilityBy: Long,
    private val ifTrueThrowTo: Int,
    private val ifFalseThrowTo: Int
) {
    private val items = ArrayDeque(startingItems)
    var numInspections = 0L
        private set

    fun takeTurn(reduce: (Long) -> Long, provideMonkey: (Int) -> Monkey) {
        while (items.isNotEmpty()) {
            items
                .removeFirst()
                .let { inspect(it) }
                .let { reduce(it) }
                .let {
                    it.throwTo(provideMonkey(if (it % testDivisibilityBy == 0L) ifTrueThrowTo else ifFalseThrowTo))
                }
        }
    }

    private fun inspect(item: Long): Long {
        numInspections++
        return operation(item)
    }

    private fun accept(item: Long) {
        items.addLast(item)
    }

    private fun Long.throwTo(other: Monkey) {
        other.accept(this)
    }
}

@Suppress("unused")
private val testMonkeys = listOf(
    //0
    Monkey(
        startingItems = listOf(79, 98),
        operation = { it * 19 },
        testDivisibilityBy = 23,
        ifTrueThrowTo = 2,
        ifFalseThrowTo = 3
    ),
    //1
    Monkey(
        startingItems = listOf(54, 65, 75, 74),
        operation = { it + 6 },
        testDivisibilityBy = 19,
        ifTrueThrowTo = 2,
        ifFalseThrowTo = 0
    ),
    //2
    Monkey(
        startingItems = listOf(79, 60, 97),
        operation = { it * it },
        testDivisibilityBy = 13,
        ifTrueThrowTo = 1,
        ifFalseThrowTo = 3
    ),
    //3
    Monkey(
        startingItems = listOf(74),
        operation = { it + 3 },
        testDivisibilityBy = 17,
        ifTrueThrowTo = 0,
        ifFalseThrowTo = 1
    )
)