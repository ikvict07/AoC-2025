package day2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import util.readLinesMmap
import kotlin.math.pow

suspend fun part1() = coroutineScope {
    val allNumbers = readLinesMmap("src/main/resources/day2/input1.txt")
        .map { line ->
            val text = line.trim(',')
            val (startStr, endStr) = text.split('-')

            val startInt = startStr.toLong()
            val endInt = endStr.toLong()

            startInt to endInt
        }
        .map { (start, end) ->
            async(Dispatchers.Default) {
                findMMByGeneration(start, end)
            }
        }
        .buffer()
        .map { deferred ->
            deferred.await()
        }
        .toList()
        .flatten()
        .sum()

    println("$allNumbers")
}

fun findMMByGeneration(start: Long, end: Long): Set<Long> {
    val result = mutableSetOf<Long>()

    val maxLen = end.toString().length / 2

    for (length in 1..maxLen) {
        val startM = 10.0.pow(length - 1).toLong()
        val endM = 10.0.pow(length).toLong() - 1

        val multiplier = 10.0.pow(length).toLong() + 1

        for (m in startM..endM) {
            val candidate = m * multiplier

            if (candidate in start..end) {
                result.add(candidate)
            }
        }
    }
    return result
}


suspend fun part2() = coroutineScope {
    val allNumbersSum = readLinesMmap("src/main/resources/day2/input1.txt")
        .map { line ->
            val text = line.trim(',')
            val (startStr, endStr) = text.split('-')
            startStr.toLong() to endStr.toLong()
        }
        .map { (start, end) ->
            async(Dispatchers.Default) {
                (start..end).filter { num -> isInvalidId(num) }
            }
        }
        .buffer()
        .map { it.await() }
        .toList()
        .flatten()
        .sum()

    println("$allNumbersSum")
}

fun isInvalidId(number: Long): Boolean {
    val s = number.toString()
    return (s + s).indexOf(s, 1) < s.length
}


suspend fun main() = part2()