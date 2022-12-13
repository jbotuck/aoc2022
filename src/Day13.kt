fun main() {
    val lines = readInput("Day13")
    //part 1
    lines.asSequence()
        .chunked(3)
        .map { it.take(2) }
        .withIndex().map { it.copy(index = it.index.inc()) }
        .filter { isValid(it.value) }
        .sumOf { it.index }
        .let { println(it) }

    //part 2
    val dividers = listOf(
        "[[2]]",
        "[[6]]"
    )
    val sorted = lines
        .filter { it.isNotBlank() }
        .plus(dividers)
        .sortedWith { a, b -> ParserPacketList(a).compareTo(ParserPacketList(b)) }
    println(sorted.indexOf(dividers.first()).inc() * sorted.indexOf(dividers.last()).inc())
}

private fun isValid(pair: List<String>): Boolean {
    val (left, right) = pair.map { ParserPacketList(it) }
    return isValid(left, right)
}

private fun isValid(left: PacketList, right: PacketList): Boolean {
    return left < right
}

private class Parser(private val packet: String) {
    var currentIndex = 1
    private fun currentValue() = packet.getOrNull(currentIndex)
    fun nextValue(): PacketValue? {
        when (currentValue()) {
            null -> return null
            '[' -> {
                currentIndex++
                return ParserPacketList(this)
            }

            ']' -> {
                currentIndex++
                if (currentValue() == ',') currentIndex++
                return null
            }

            else -> {
                return sequence {
                    while (currentValue() !in setOf(',', ']', null)) {
                        yield(currentValue()!!)
                        currentIndex++
                    }
                    if (currentValue() == ',') currentIndex++
                }.joinToString("").toInt().let { PacketInt(it) }
            }
        }
    }
}

private sealed interface PacketValue : Comparable<PacketValue>
private data class PacketInt(val value: Int) : PacketValue {
    override fun compareTo(other: PacketValue): Int {
        if (other is PacketInt) return value.compareTo(other.value)
        return IntPacketList(value).compareTo(other)
    }
}

private interface PacketList : PacketValue {
    fun nextValue(): PacketValue?
    override fun compareTo(other: PacketValue): Int {
        when (other) {
            is PacketInt -> return compareTo(IntPacketList(other.value))
            is PacketList -> {
                var left: PacketValue?
                while (nextValue().also { left = it } != null) {
                    val right = other.nextValue() ?: return 1
                    left!!.compareTo(right).takeIf { it != 0 }?.let { return it }
                }
                return if (other.nextValue() != null) -1 else 0
            }
        }
    }
}

private class ParserPacketList(private val parser: Parser) : PacketList {
    constructor(packet: String) : this(Parser(packet))

    override fun nextValue() = parser.nextValue()
}

private class IntPacketList(private val value: Int) : PacketList {
    var hasNext = true
    override fun nextValue() = if (hasNext) PacketInt(value).also { hasNext = false } else null
}
