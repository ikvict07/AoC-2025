package day10

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import util.readLinesMmap
import java.util.ArrayDeque
import kotlin.math.abs
import kotlin.system.measureTimeMillis


suspend fun part1() {
    readLinesMmap("src/main/resources/day10/test.txt").map { line ->
        val components = line.split(' ')
        val targetState = parseState(components[0])
        val initialState = parseState("[....]")

        val switches = components.drop(1).dropLast(1).map {
            it to maskOf(*it.trim('(', ')').split(',').map { it.toInt() }.toIntArray())
        }
        val result = solveLights(initialState, targetState, switches)
        result?.size ?: 0
    }.toList().sum().also(::println)
}

suspend fun main() {
    measureTimeMillis {
        part2()
    }.also(::println)
}

suspend fun part2() {
    readLinesMmap("src/main/resources/day10/input.txt").map { line ->
        val components = line.split(' ')
        val componentsFiltered = components.drop(1)

        val target = componentsFiltered.last().trim('{', '}').split(',').map { it.toLong() }.toLongArray()

        val buttonsEffects = componentsFiltered.dropLast(1).map {
            it.trim('(', ')').split(',').map { idx -> idx.toInt() }
        }

        val numRows = target.size
        val numCols = buttonsEffects.size
        val matrix = Array(numRows) { LongArray(numCols) }

        buttonsEffects.forEachIndexed { btnIdx, affectedRows ->
            affectedRows.forEach { rowIdx ->
                if (rowIdx < numRows) {
                    matrix[rowIdx][btnIdx] = 1L
                }
            }
        }

        val solver = IntegerSolver(matrix, target)
        val ans = solver.solveMinPresses()
        ans!!
    }.toList().sum().also(::println)
}


class IntegerSolver(
    val matrix: Array<LongArray>,
    val targets: LongArray
) {
    fun solveMinPresses(): Long? {
        val rows = matrix.size
        val cols = matrix[0].size

        val aug = Array(rows) { r ->
            DoubleArray(cols + 1) { c ->
                if (c < cols) matrix[r][c].toDouble() else targets[r].toDouble()
            }
        }

        var pivotRow = 0
        val pivotCols = IntArray(rows) { -1 }
        val freeVars = mutableListOf<Int>()

        for (col in 0 until cols) {
            if (pivotRow >= rows) {
                freeVars.add(col)
                continue
            }

            var maxRow = pivotRow
            for (r in pivotRow + 1 until rows) {
                if (abs(aug[r][col]) > abs(aug[maxRow][col])) maxRow = r
            }

            if (abs(aug[maxRow][col]) < 1e-9) {
                freeVars.add(col)
                continue
            }

            val temp = aug[pivotRow]
            aug[pivotRow] = aug[maxRow]
            aug[maxRow] = temp

            val pivotVal = aug[pivotRow][col]
            for (c in col until cols + 1) aug[pivotRow][c] /= pivotVal

            for (r in 0 until rows) {
                if (r != pivotRow) {
                    val factor = aug[r][col]
                    for (c in col until cols + 1) aug[r][c] -= factor * aug[pivotRow][c]
                }
            }

            pivotCols[pivotRow] = col
            pivotRow++
        }

        for (r in pivotRow until rows) {
            if (abs(aug[r][cols]) > 1e-9) return null
        }


        var minTotalPresses = Long.MAX_VALUE
        var foundSolution = false

        fun search(freeVarIndex: Int, currentAssigns: DoubleArray) {
            if (freeVarIndex == freeVars.size) {
                val finalX = currentAssigns.clone()

                for (r in pivotRow - 1 downTo 0) {
                    val pCol = pivotCols[r]
                    var sum = aug[r][cols]
                    for (c in pCol + 1 until cols) {
                        sum -= aug[r][c] * finalX[c]
                    }
                    finalX[pCol] = sum
                }

                var currentTotal = 0L
                for (x in finalX) {
                    val rounded = Math.round(x)
                    if (abs(x - rounded) > 1e-5) return

                    if (rounded < 0) return

                    currentTotal += rounded
                }

                if (currentTotal < minTotalPresses) {
                    minTotalPresses = currentTotal
                    foundSolution = true
                }
                return
            }

            val colIdx = freeVars[freeVarIndex]
            for (value in 0..250) {
                currentAssigns[colIdx] = value.toDouble()
                search(freeVarIndex + 1, currentAssigns)
            }
        }

        search(0, DoubleArray(cols))

        return if (foundSolution) minTotalPresses else null
    }
}

fun solveLights(
    start: Int,
    target: Int,
    moves: List<Pair<String, Int>>
): List<String>? {
    val queue = ArrayDeque<Pair<Int, List<String>>>()

    val visited = BooleanArray(1024)

    queue.add(start to emptyList())
    visited[start] = true

    while (queue.isNotEmpty()) {
        val (current, path) = queue.removeFirst()

        if (current == target) {
            return path
        }

        for ((name, mask) in moves) {
            val next = current xor mask

            if (!visited[next]) {
                visited[next] = true
                queue.add(next to (path + name))
            }
        }
    }
    return null
}

fun parseState(s: String): Int {
    var res = 0
    val clean = s.filter { it == '.' || it == '#' }
    clean.forEachIndexed { index, char ->
        if (char == '#') {
            res = res or (1 shl index)
        }
    }
    return res
}

fun maskOf(vararg indices: Int): Int {
    var res = 0
    for (i in indices) {
        res = res or (1 shl i)
    }
    return res
}