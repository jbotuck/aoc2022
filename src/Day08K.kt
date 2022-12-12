import kotlinx.coroutines.*
import java.time.Duration
import java.time.Instant

fun main() {
    val started = Instant.now()

    runBlocking(Dispatchers.Default) {
        val forest = readInput("Day08")
            .map { line -> line.map { it.digitToInt() } }
        listOf(
            async { forest.visibleFromTop() },
            async { forest.visibleFromBottom() },
            async { forest.visibleFromLeft() },
            async { forest.visibleFromRight() }
        ).awaitAll().reduce { a, b -> a.union(b) }.let { println(it.size) }

        listOf(
            async { forest.treeScoreTop() },
            async { forest.treeScoreBottom() },
            async { forest.treeScoreLeft() },
            async { forest.treeScoreRight() }
        ).awaitAll()
            .reduce { matrix1, matrix2 ->
                matrix1.zip(matrix2) { line1, line2 ->
                    line1.zip(line2, Int::times)
                }
            }.flatten().max().let { println(it) }
    }
    println(Duration.between(started, Instant.now()).toMillis())
}

private suspend fun List<List<Int>>.treeScoreTop(): List<List<Int>> = coroutineScope {
    first().indices.map { x -> async { treeScoreTop(x) } }.awaitAll()
        .fold(Array<MutableList<Int>>(size) { mutableListOf() }) { acc, list ->
            list.forEachIndexed { y, i ->
                acc[y].add(i)
            }
            acc
        }.toList()
}

private suspend fun List<List<Int>>.treeScoreBottom(): List<List<Int>> = coroutineScope {
    first().indices.map { x -> async { treeScoreBottom(x) } }.awaitAll()
        .fold(Array<MutableList<Int>>(size) { mutableListOf() }) { acc, list ->
            list.forEachIndexed { y, i ->
                acc[y].add(i)
            }
            acc
        }.toList()
}

private suspend fun List<List<Int>>.treeScoreLeft(): List<List<Int>> = coroutineScope {
    map { async { it.treeScore() } }.awaitAll()
}

private suspend fun List<List<Int>>.treeScoreRight(): List<List<Int>> = coroutineScope {
    map { async { it.reversed().treeScore().reversed() } }.awaitAll()
}

private fun List<List<Int>>.treeScoreTop(x: Int): List<Int> = map { it[x] }.treeScore()

private fun List<List<Int>>.treeScoreBottom(x: Int): List<Int> = map { it[x] }.reversed().treeScore().reversed()


private fun List<Int>.treeScore(): List<Int> {
    val visibleTrees = ArrayDeque<Pair<Int, Int>>()
    return mapIndexed { index, treeSize ->
        while (visibleTrees.isNotEmpty() && visibleTrees.last().second < treeSize) visibleTrees.removeLast()
        val blockingTree = visibleTrees.lastOrNull()
        visibleTrees.addLast(index to treeSize)
        blockingTree?.let { index - blockingTree.first } ?: index
    }
}

private suspend fun List<List<Int>>.visibleFromTop() = coroutineScope {
    first().indices.map { x ->
        async {
            visibleFromTop(x).map { it to x }
        }
    }.awaitAll()
        .flatten()
        .toSet()
}

private suspend fun List<List<Int>>.visibleFromLeft() = coroutineScope {
    indices.map { y ->
        async {
            visibleFromLeft(y).map { y to it }
        }
    }.awaitAll()
        .flatten()
        .toSet()
}

private suspend fun List<List<Int>>.visibleFromRight() = coroutineScope {
    indices.map { y ->
        async {
            visibleFromRight(y).map { y to it }
        }
    }.awaitAll()
        .flatten()
        .toSet()
}

private suspend fun List<List<Int>>.visibleFromBottom() = coroutineScope {
    first().indices.map { x ->
        async {
            visibleFromBottom(x).map { it to x }
        }
    }.awaitAll()
        .flatten()
        .toSet()
}


private fun List<List<Int>>.visibleFromTop(x: Int): List<Int> {
    var max = -1
    return mutableListOf<Int>().also {
        for (indexedLine in withIndex()) {
            if (indexedLine.value[x] > max) {
                it.add(indexedLine.index)
                max = indexedLine.value[x]
            }
        }
    }
}

private fun List<List<Int>>.visibleFromBottom(x: Int): List<Int> {
    var max = -1
    return mutableListOf<Int>().also {
        for (indexedLine in withIndex().reversed()) {
            if (indexedLine.value[x] > max) {
                it.add(indexedLine.index)
                max = indexedLine.value[x]
            }
        }
    }
}

private fun List<List<Int>>.visibleFromLeft(y: Int): List<Int> {
    var max = -1
    return mutableListOf<Int>().also {
        for (indexedInt in get(y).withIndex()) {
            if (indexedInt.value > max) {
                it.add(indexedInt.index)
                max = indexedInt.value
            }
        }
    }
}

private fun List<List<Int>>.visibleFromRight(y: Int): List<Int> {
    var max = -1
    return mutableListOf<Int>().also {
        for (indexedInt in get(y).withIndex().reversed()) {
            if (indexedInt.value > max) {
                it.add(indexedInt.index)
                max = indexedInt.value
            }
        }
    }
}


