fun main() {
    val lines = readInput("Day05")
    val indexOfStackKeyLine = lines.indexOfFirst { it.trim().first() == '1' }
    val part1 = part1(lines, indexOfStackKeyLine)
    val part2 = part2(lines, indexOfStackKeyLine)
    println("part 1 $part1")
    println("part 2 $part2")
}

private fun part1(lines: List<String>, indexOfStackKeyLine: Int): String {
    return solve(lines, indexOfStackKeyLine, 1)
}

private fun part2(lines: List<String>, indexOfStackKeyLine: Int): String {
    return solve(lines, indexOfStackKeyLine, 2)
}

private fun solve(lines: List<String>, indexOfStackKeyLine: Int, part: Int): String {
    val stacks = parseStacks(lines.subList(0, indexOfStackKeyLine).asReversed(), lines[indexOfStackKeyLine])
    for (line in lines.subList(indexOfStackKeyLine + 2, lines.size)) {
        stacks.parseInstruction(line).execute(part)
    }
    return topOfEach(stacks)
}

fun parseStacks(lines: List<String>, key: String) = mutableMapOf<Char, ArrayDeque<Char>>()
    .apply {
        key.forEachIndexed { index, c ->
            if (c.isDigit()) {
                val stack = ArrayDeque<Char>()
                for (line in lines) {
                    line.getOrNull(index)?.takeIf { it.isUpperCase() }?.let { stack.addLast(it) } ?: break
                }
                this[c] = stack
            }
        }
    }.toMap()

private fun Map<Char, ArrayDeque<Char>>.parseInstruction(line: String): Instruction {
    val split = line.split(" ")
    return Instruction(
        count = split[1].toInt(),
        from = get(split[3].first())!!,
        to = get(split[5].first())!!
    )
}

private fun topOfEach(stacks: Map<Char, ArrayDeque<Char>>) = stacks.keys.sorted()
    .map { stacks[it]!!.last() }
    .joinToString("")

data class Instruction(val count: Int, val from: ArrayDeque<Char>, val to: ArrayDeque<Char>) {
    private fun execute1() {
        repeat(count) {
            to.addLast(from.removeLast())
        }
    }

    private fun execute2() {
        to.addAll(from.takeLast(count))
        from.subList(from.size - count, from.size).clear()
    }

    fun execute(part: Int) {
        if (part == 1) execute1() else execute2()
    }
}