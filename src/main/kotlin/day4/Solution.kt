package day4

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import util.readLinesMmap
import kotlin.system.measureTimeMillis

fun <T> Flow<T>.windowed(size: Int): Flow<List<T>> = flow {
    val buffer = ArrayDeque<T>(size)
    collect { item ->
        buffer.add(item)
        if (buffer.size > size) buffer.removeFirst()

        if (buffer.size == size) {
            emit(buffer.toList())
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun part1() {
    var total = 0

    readLinesMmap("src/main/resources/day4/input.txt")
        .onStart { emit("") }
        .onCompletion { emit("") }
        .windowed(3)
        .collect { window ->
            val (prev, curr, next) = window

            if (curr.isNotEmpty()) {
                total += process(prev, curr, next)
            }
        }

    println(total)
}

fun process(first: String, middle: String, last: String): Int {
    return middle.indices.count { index ->
        if (middle[index] != '@') return@count false

        val neighborsCount = getAdjacent8(first, middle, last, index)
            .count { it == '@' }

        neighborsCount < 4
    }
}


fun getAdjacent8(first: String, middle: String, last: String, charIndex: Int): List<Char> {
    val result = mutableListOf<Char>()

    result.add(if (first.isEmpty() || charIndex == 0) '.' else first.getOrElse(charIndex - 1) { '.' })
    result.add(if (first.isEmpty()) '.' else first.getOrElse(charIndex) { '.' })
    result.add(if (first.isEmpty() || charIndex == middle.length - 1) '.' else first.getOrElse(charIndex + 1) { '.' })

    result.add(if (charIndex == 0) '.' else middle.getOrElse(charIndex - 1) { '.' })
    result.add(if (charIndex == middle.length - 1) '.' else middle.getOrElse(charIndex + 1) { '.' })

    result.add(if (last.isEmpty() || charIndex == 0) '.' else last.getOrElse(charIndex - 1) { '.' })
    result.add(if (last.isEmpty()) '.' else last.getOrElse(charIndex) { '.' })
    result.add(if (last.isEmpty() || charIndex == middle.length - 1) '.' else last.getOrElse(charIndex + 1) { '.' })

    return result

}

suspend fun part2() {
    val grid: List<CharArray> = readLinesMmap("src/main/resources/day4/input.txt")
        .map { it.toCharArray() }
        .toList()

    val height = grid.size
    val width = grid[0].size
    var totalRemoved = 0

    while (true) {
        val toRemove = mutableListOf<Pair<Int, Int>>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (grid[y][x] == '@') {
                    val neighbors = countNeighbors(grid, x, y, width, height)
                    if (neighbors < 4) {
                        toRemove.add(x to y)
                    }
                }
            }
        }

        if (toRemove.isEmpty()) break

        toRemove.forEach { (x, y) ->
            grid[y][x] = '.'
        }

        totalRemoved += toRemove.size
    }

}

fun countNeighbors(grid: List<CharArray>, x: Int, y: Int, w: Int, h: Int): Int {
    var count = 0
    for (dy in -1..1) {
        for (dx in -1..1) {
            if (dx == 0 && dy == 0) continue

            val nx = x + dx
            val ny = y + dy

            if (nx in 0 until w && ny in 0 until h && grid[ny][nx] == '@') {
                count++
            }
        }
    }
    return count
}

suspend fun main() {
    println(measureTimeMillis {
        part2()

    })
}