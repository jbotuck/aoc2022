import java.math.BigInteger

fun main() {
    val lines = readInput("Day07")
    solve(lines)
}

private fun solve(lines: List<String>) {
    @Suppress("NAME_SHADOWING")
    val lines = ArrayDeque(lines)
    val lastVisited = ArrayDeque<SystemFile>()
    val root = SystemFile("root", null, mutableMapOf())
    val allFiles = mutableListOf<SystemFile>()
    while (lines.isNotEmpty()) {
        when (val line = lines.removeFirst()) {
            "\$ ls" -> {
                lines
                    .takeWhile { !it.startsWith('$') }
                    .also { lines.subList(0, it.size).clear() }
                    .let { lastVisited.last().updateWithLsOutput(it) }
                    .let { allFiles.addAll(it) }
            }

            "\$ cd .." -> {
                lastVisited.removeLast()
                lastVisited.lastOrNull()?.recalculateSize()
            }

            "\$ cd /" -> { //only happens once in my input so not bothering to back up one level at a time
                lastVisited.clear()
                lastVisited.add(root)
            }

            else -> lastVisited.add(lastVisited.last().cd(line.substringAfter("\$ cd ")))
        }
    }
    while (lastVisited.isNotEmpty()) lastVisited.removeLast().apply { recalculateSize() }
    val part1 =
        allFiles.filter { it.children != null }.map { it.size!! }.filter { it < BigInteger("100000") }.sumOf { it }
    println("part 1 $part1")
    val freeSpaceStillNeeded = BigInteger("30000000").minus(BigInteger("70000000").minus(root.size!!))
    val part2 = allFiles.filter { it.children != null }.map { it.size!! }.filter { it >= freeSpaceStillNeeded }.min()
    println("part 2 $part2")

}

private data class SystemFile(
    val name: String,
    var size: BigInteger? = null,
    val children: MutableMap<String, SystemFile>? = null
) {

    companion object {
        fun of(lsOutput: String): SystemFile {
            val split = lsOutput.split(" ")
            if (split.first() == "dir") return SystemFile(split.last(), children = mutableMapOf())
            return SystemFile(split.last(), BigInteger(split.first()))
        }
    }

    fun updateWithLsOutput(files: List<String>): List<SystemFile> {
        return files
            .map { of(it) }
            .filter { it.name !in children!!.keys }
            .also { list ->
                for (file in list) children!![file.name] = file
                recalculateSize()
            }
    }

    fun recalculateSize() {
        if (size != null) return
        if (children!!.values.all { it.size != null }) size = children.values.sumOf { it.size!! }
    }

    fun cd(directory: String): SystemFile {
        return children!![directory] ?: SystemFile(directory, children = mutableMapOf()).also {
            children[directory] = it
        }
    }
}