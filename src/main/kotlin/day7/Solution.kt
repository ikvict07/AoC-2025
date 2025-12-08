package day7


import java.io.File
import java.util.ArrayDeque

data class Point(val y: Int, val x: Int)

fun part1() {
    val lines = File("src/main/resources/day7/test.txt").readLines()
    if (lines.isEmpty()) return

    val startX = lines[0].indexOf('S')
    val activatedSplitters = HashSet<Point>()

    val queue = ArrayDeque<Point>()

    queue.add(Point(0, startX))

    val height = lines.size
    val width = lines[0].length

    var cunter = 0
    while (queue.isNotEmpty()) {
        val (startY, startX) = queue.removeFirst()

        for (y in startY until height) {
            if (startX < 0 || startX >= width) break

            val char = lines[y][startX]

            if (char == '^') {
                val splitter = Point(y, startX)
                if (splitter !in activatedSplitters) {
                    activatedSplitters.add(splitter)
                    println("Timeline: ${++cunter}")


                    queue.add(Point(y, startX - 1))
                    queue.add(Point(y, startX + 1))
                }

                break
            }
        }
    }

    println("Total splits: ${activatedSplitters.size}")
}
fun part2() {
    val lines = File("src/main/resources/day7/input.txt").readLines()
    if (lines.isEmpty()) return

    val startX = lines[0].indexOf('S')

    val memo = HashMap<Point, Long>()

    val totalTimelines = countTimelines(lines, 0, startX, memo)

    println("Total timelines: $totalTimelines")
}

fun countTimelines(
    grid: List<String>,
    y: Int,
    x: Int,
    memo: MutableMap<Point, Long>
): Long {
    val height = grid.size
    val width = grid[0].length

    for (currY in y until height) {
        if (x !in 0..<width) return 1L

        val char = grid[currY][x]

        if (char == '^') {
            val splitterPos = Point(currY, x)

            if (memo.containsKey(splitterPos)) {
                return memo[splitterPos]!!
            }

            val leftPaths = countTimelines(grid, currY, x - 1, memo)
            val rightPaths = countTimelines(grid, currY, x + 1, memo)

            val total = leftPaths + rightPaths

            memo[splitterPos] = total

            return total
        }
    }

    return 1L
}

fun main() {
    part2()
}