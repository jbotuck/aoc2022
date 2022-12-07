import java.math.BigInteger

fun main() {
    val lines = readInput("Day07")
    solve(lines)
}

private fun solve(lines: List<String>) {
    @Suppress("NAME_SHADOWING")
    val lines = ArrayDeque(lines)
    val lastVisited = ArrayDeque<Directory>()
    val root = Directory("root")
    val allFiles = mutableListOf<SystemObject>()
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
                lastVisited.add(root)
            }

            else -> lastVisited.add(lastVisited.last().cd(line.substringAfter("\$ cd ")))
        }
    }
    while (lastVisited.isNotEmpty()) lastVisited.removeLast().apply { recalculateSize() }
    val part1 =
        allFiles
            .filterIsInstance<Directory>()
            .map { it.size!! }
            .filter { it < BigInteger("100000") }
            .sumOf { it }
    println("part 1 $part1")
    val freeSpaceStillNeeded = BigInteger("30000000").minus(BigInteger("70000000").minus(root.size!!))
    val part2 = allFiles
        .filterIsInstance<Directory>()
        .map { it.size!! }
        .filter { it >= freeSpaceStillNeeded }
        .min()
    println("part 2 $part2")

}

private sealed class SystemObject(
    val name: String,
    open val size: BigInteger?
) {
    companion object {
        fun of(lsOutput: String): SystemObject {
            val split = lsOutput.split(" ")
            if (split.first() == "dir") return Directory(split.last())
            return DataFile(split.last(), BigInteger(split.first()))
        }
    }
}

private class DataFile(name: String, size: BigInteger) : SystemObject(name, size)

private class Directory(name: String) : SystemObject(name, null) {
    override var size: BigInteger? = super.size
    val children = mutableMapOf<String, SystemObject>()
    fun updateWithLsOutput(files: List<String>): List<SystemObject> {
        return files
            .map { of(it) }
            .filter { it.name !in children.keys }
            .also { list ->
                for (file in list) children[file.name] = file
                recalculateSize()
            }
    }

    fun recalculateSize() {
        if (size != null) return
        size = children.values.sumOf { it.size ?: return }
    }

    fun cd(directory: String): Directory {
        return children[directory] as? Directory ?: Directory(directory).also {
            children[directory] = it
        }
    }
}