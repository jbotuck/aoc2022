fun main() {
    val lines = readInput("Day04")
    val rangePairs = lines.map { line ->
        line.split(",")
            .map { elf ->
                elf.split("-")
                    .map { it.toInt() }
                    .let { it.first()..it.last() }
            }
    }
    val part1 = rangePairs.count { ranges ->
        ranges.first().all { it in ranges.last() } || ranges.last().all { it in ranges.first() }
    }
    val part2 = rangePairs.count { ranges ->
        ranges.first().any { it in ranges.last() }
    }
    println("part 1 $part1")
    println("part 2 $part2")
}

