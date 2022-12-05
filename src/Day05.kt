fun main() {
    val lines = readInput("Day05")
    val indexOfStackKeyLine = lines.indexOfFirst { it.trim().first() == '1' }
    val part1 = part1(lines, indexOfStackKeyLine)
    val part2 = part2(lines, indexOfStackKeyLine)
    println("part 1 $part1")
    println("part 2 $part2")
}

private fun part1(lines: List<String>, indexOfStackKeyLine: Int): String {
    val stacks = parseStacks(lines.subList(0, indexOfStackKeyLine).asReversed(), lines[indexOfStackKeyLine])
    for (line in lines.subList(indexOfStackKeyLine + 2, lines.size)) {
        stacks.execute1(line)
    }
    return topOfEach(stacks)
}

private fun part2(lines: List<String>, indexOfStackKeyLine: Int): String {
    val stacks = parseStacks(lines.subList(0, indexOfStackKeyLine).asReversed(), lines[indexOfStackKeyLine])
    for (line in lines.subList(indexOfStackKeyLine + 2, lines.size)) {
        stacks.execute2(line)
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

private fun Map<Char, ArrayDeque<Char>>.execute1(line: String) {
    val split = line.split(" ")
    val count = split[1].toInt()
    val from = get(split[3].first())!!
    val to = get(split[5].first())!!
    repeat(count) {
        to.addLast(from.removeLast())
    }
}

private fun Map<Char, ArrayDeque<Char>>.execute2(line: String) {
    val split = line.split(" ")
    val count = split[1].toInt()
    val from = get(split[3].first())!!
    val to = get(split[5].first())!!
    to.addAll(from.takeLast(count))
    from.subList(from.size - count, from.size).clear()
}

private fun topOfEach(stacks: Map<Char, ArrayDeque<Char>>) = stacks.keys.sorted()
    .map { stacks[it]!!.last() }
    .joinToString("")