fun main() {
    val lines = readInput("Day10")
    val cpu = CPU()
    cpu.execute(lines)//prints part2
    println()
    println(cpu.part1Result())
}

class CPU {
    private var x = 1
    private var cyclesRan = 0
    private val part1Values = mutableListOf<Int>()
    fun part1Result() = part1Values.sum()
    fun execute(lines: List<String>) {
        for (line in lines) {
            tick()
            if (line != "noop") {
                tick()
                x += line.split(" ").last().toInt()
            }
        }
    }

    private fun tick() {
        val position = cyclesRan % 40
        if (position == 0) println()
        if (position in x.dec()..x.inc()) print('⚫') else print('⚪')
        cyclesRan++
        if (cyclesRan in listOf(20, 60, 100, 140, 180, 220)) {
            part1Values.add(x * cyclesRan)
        }
    }
}