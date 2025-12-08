package day5

import util.readLinesMmap
import kotlin.system.measureTimeMillis

suspend fun part1() {
    val intervals = mutableListOf<LongRange>()
    var isFirst = true
    val ids = mutableSetOf<Long>()
    readLinesMmap("src/main/resources/day5/input.txt").collect { line ->
        if (line.isBlank()) {
            isFirst = false
            return@collect
        }

        if (isFirst) {
            val (start, end) = line.split('-').map { it.toLong() }
            intervals.add(start..end)
        } else {
            val id = line.toLong()
            ids.add(id)
        }
    }

    intervals.sortBy { it.first }

    val mergedIntervals = buildList {
        if (intervals.isNotEmpty()) {
            var current = intervals[0]

            for (i in 1 until intervals.size) {
                val next = intervals[i]

                if (next.first <= current.last) {
                    val newEnd = maxOf(current.last, next.last)
                    current = current.first..newEnd
                } else {
                    add(current)
                    current = next
                }
            }
            add(current)
        }
    }


    println(ids.count { id -> mergedIntervals.any { it.contains(id) } })
}


suspend fun part2() {
    val intervals = ArrayList<LongRange>(1000)
    var isFirst = true

    readLinesMmap("src/main/resources/day5/input.txt").collect { line ->
        if (line.isBlank()) {
            isFirst = false
            return@collect
        }

        if (isFirst) {
            val dashIndex = line.indexOf('-')
            val start = line.substring(0, dashIndex).toLong()
            val end = line.substring(dashIndex + 1).toLong()
            intervals.add(start..end)
        }
    }

    if (intervals.isEmpty()) {
        println(0)
        return
    }

    intervals.sortBy { it.first }

    var totalCount = 0L

    var currentStart = intervals[0].first
    var currentEnd = intervals[0].last

    for (i in 1 until intervals.size) {
        val next = intervals[i]
        val nextStart = next.first
        val nextEnd = next.last

        if (nextStart <= currentEnd) {
            if (nextEnd > currentEnd) {
                currentEnd = nextEnd
            }
        } else {
            totalCount += (currentEnd - currentStart + 1)

            currentStart = nextStart
            currentEnd = nextEnd
        }
    }

    totalCount += (currentEnd - currentStart + 1)

    println(totalCount)
}
suspend fun main() {
    println(measureTimeMillis {
        part2()

    })
}