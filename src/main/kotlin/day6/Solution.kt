package day6

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import util.readLinesMmap
import java.io.File


suspend fun part1() {
    val matrix: List<List<String>> = readLinesMmap("src/main/resources/day6/input.txt")
        .map { line ->
            line.trim().split("\\s+".toRegex())
        }.toList()

    if (matrix.isEmpty()) return

    val width = matrix[0].size

    val transposed: List<List<String>> = (0 until width).map { colIndex ->
        matrix.map { row -> row[colIndex] }
    }

    transposed.asFlow().map {
        val last = it.last()
        if (last == "*") {
            it.dropLast(1).map { it.toLong() }.reduce { acc, i -> acc * i }
        } else {
            it.dropLast(1).sumOf { it.toLong() }
        }
    }.toList().sum().also(::println)
}

suspend fun part2() {
    val lines = File("src/main/resources/day6/input.txt").readLines()

    if (lines.isEmpty()) return

    val maxWidth = lines.maxOf { it.length }
    val matrix = lines.map { it.padEnd(maxWidth, ' ') }
    val height = matrix.size

    val currentProblemCols = mutableListOf<Int>()
    var grandTotal = 0L


    for (col in 0 until maxWidth) {
        val isSeparator = (0 until height).all { row -> matrix[row][col] == ' ' }

        if (isSeparator) {
            if (currentProblemCols.isNotEmpty()) {
                grandTotal += solveProblem(currentProblemCols, matrix)
                currentProblemCols.clear()
            }
        } else {
            currentProblemCols.add(col)
        }
    }
    if (currentProblemCols.isNotEmpty()) {
        grandTotal += solveProblem(currentProblemCols, matrix)
    }

    println("Grand Total: $grandTotal")
}

fun solveProblem(cols: List<Int>, matrix: List<String>): Long {
    val numbers = mutableListOf<Long>()
    var operator = '+' //

    for (col in cols) {
        val sb = StringBuilder()
        for (row in 0 until matrix.size - 1) {
            val char = matrix[row][col]
            if (char != ' ') sb.append(char)
        }

        if (sb.isNotEmpty()) {
            numbers.add(sb.toString().toLong())
        }

        val bottomChar = matrix.last()[col]
        if (bottomChar != ' ' && !bottomChar.isDigit()) {
            operator = bottomChar
        }
    }

    if (numbers.isEmpty()) return 0L

    var result = numbers[0]
    for (i in 1 until numbers.size) {
        when (operator) {
            '+' -> result += numbers[i]
            '*' -> result *= numbers[i]
            else -> error("Unknown operator: $operator")
        }
    }

    return result
}

suspend fun main() {
    part2()
}