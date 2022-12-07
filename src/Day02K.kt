@file:OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import java.time.Duration
import java.time.Instant

fun main() {
    val started = Instant.now()
    runBlocking(Dispatchers.Default) {
        val lines = readInput("Day02")
        val channel = produce(capacity = 64) {
            for (line in lines) send(line)
        }
        val concurrency = 8
        val consumers = (1..concurrency).map {
            sumActor {
                score1(it)
            }
        }
        coroutineScope {
            for (consumer in consumers) {
                launch {
                    for (line in channel) {
                        consumer.send(LineMsg(line))
                    }
                }
            }
        }


        val part1 = consumers.sumOf { actor ->
            CompletableDeferred<Int>().also {
                actor.send(GetResult(it))
            }.await()
        }
        for (consumer in consumers) consumer.close()
        val part2 = lines.sumOf { score2(it) }
        println("part 1 $part1")
        println("part 2 $part2")
    }
    println(Duration.between(started, Instant.now()).toMillis())
}


private val rules = setOf(0 to 2, 1 to 0, 2 to 1)//who beats who

private fun Pair<Int, Int>.score(opponent: Int) = 1 + first + when (opponent) {
    second -> 6
    first -> 3
    else -> 0
}

private fun score1(line: String) = rules
    .find { it.first == line.last().code % 'X'.code }!!
    .score(line.first().code % 'A'.code)

private fun score2(line: String): Int {
    val opponent = line.first().code % 'A'.code
    return when (line.last()) {
        'X' -> rules.find { it.first == opponent }!!.second.let { self -> rules.find { it.first == self } }
        'Y' -> rules.find { it.first == opponent }
        else -> rules.find { it.second == opponent }
    }!!.score(opponent)
}

private fun CoroutineScope.sumActor(score: suspend (String) -> Int) = actor<SumMsg>(capacity = 64) {
    var sum = 0 // actor state
    for (msg in channel) { // iterate over incoming messages
        when (msg) {
            is LineMsg -> sum += score(msg.line)
            is GetResult -> msg.response.complete(sum)
        }
    }
}

private sealed class SumMsg
private data class LineMsg(val line: String) : SumMsg()
private class GetResult(val response: CompletableDeferred<Int>) : SumMsg()