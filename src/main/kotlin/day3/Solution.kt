package day3


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import util.readBytes
import util.readLinesMmap

suspend fun part1() = coroutineScope {
    val results = readLinesMmap("src/main/resources/day3/input.txt")
        .map { line ->
            async(Dispatchers.Default) {
                var maxJoltage = 0
                var maxRightDigit = -1

                for (i in line.indices.reversed()) {
                    val digit = line[i] - '0'
                    if (maxRightDigit != -1) {
                        val currentJoltage = digit * 10 + maxRightDigit
                        if (currentJoltage > maxJoltage) maxJoltage = currentJoltage
                    }
                    if (digit > maxRightDigit) maxRightDigit = digit
                }
                maxJoltage
            }
        }
        .buffer()
        .map { it.await() }
        .toList()
        .sum()

    println(results)
}


inline fun part2() {
    var totalSum = 0L

    val lineBuffer = IntArray(2048)
    var lineLength = 0

    readBytes("src/main/resources/day3/input.txt") { byte ->
        when (byte) {
            10.toByte() -> {
                if (lineLength > 0) {
                    totalSum += solveFromBuffer(lineBuffer, lineLength)
                    lineLength = 0
                }
            }

            else -> {
                lineBuffer[lineLength++] = byte - 48
            }
        }
    }

    if (lineLength > 0) {
        totalSum += solveFromBuffer(lineBuffer, lineLength)
    }

    println("Total Sum: $totalSum")
}

inline fun solveFromBuffer(buffer: IntArray, length: Int): Long {
    if (length < 12) return 0L

    var result = 0L
    var currentIndex = 0
    var needed = 12

    repeat(12) {
        val searchEnd = length - needed

        var maxDigit = -1
        var maxDigitIndex = -1

        for (i in currentIndex..searchEnd) {
            val digit = buffer[i]

            if (digit == 9) {
                maxDigit = 9
                maxDigitIndex = i
                break
            }

            if (digit > maxDigit) {
                maxDigit = digit
                maxDigitIndex = i
            }
        }

        result = result * 10 + maxDigit

        currentIndex = maxDigitIndex + 1
        needed--
    }

    return result
}

fun main() {
    part2()
}