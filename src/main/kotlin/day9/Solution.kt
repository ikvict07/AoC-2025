package day9

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import util.readLinesMmap
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Path2D
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.system.measureTimeMillis


suspend fun part1() {
    val coords = readLinesMmap("src/main/resources/day9/input.txt").map {
        val (first, second) = it.split(',')
        first.toInt() to second.toInt()
    }.toList()

    val maxX = coords.maxBy { it.first }.first
    val maxY = coords.maxBy { it.second }.second
    val minX = coords.minBy { it.first }.first
    val minY = coords.minBy { it.second }.second

    val boundary = coords.filter { it.first == maxX || it.first == minX || it.second == maxY || it.second == minY }

    var maxArea = 0L
    var coord1 = 0 to 0
    var coord2 = 0 to 0

    coords.forEach { outer ->
        coords.forEach { inner ->
            if (outer == inner) return@forEach
            val area = getArea(outer, inner)
            if (area > maxArea) {
                coord1 = outer
                coord2 = inner
                maxArea = area
            }
        }
    }
    println(getArea(coord1, coord2))
    println(maxArea)
}

fun getArea(start: Pair<Int, Int>, end: Pair<Int, Int>): Long {
    val h = (start.second - end.second).absoluteValue + 1
    val w = (start.first - end.first).absoluteValue + 1
    return h * w.toLong()
}

fun visualize(data: List<Pair<Int, Int>>) {
    val maxX = data.maxOf { it.first }
    val maxY = data.maxOf { it.second }

    val grid = Array(maxY + 2) { CharArray(maxX + 3) { '.' } }

    for ((x, y) in data) {
        grid[y][x] = '#'
    }
    for (row in grid) {
        println(row)
    }
}


data class Point(val x: Int, val y: Int)

suspend fun part2() {
    val coords = readLinesMmap("src/main/resources/day9/input.txt").map {
        val (first, second) = it.split(',')
        Point(first.toInt(), second.toInt())
    }.toList()

    val polygon = Path2D.Double()
    val start = coords.first()
    polygon.moveTo(start.x.toDouble(), start.y.toDouble())

    for (i in 1 until coords.size) {
        polygon.lineTo(coords[i].x.toDouble(), coords[i].y.toDouble())
    }
    polygon.closePath()


    var maxArea = 0L

    for (i in coords.indices) {
        for (j in i + 1 until coords.size) {
            val p1 = coords[i]
            val p2 = coords[j]

            val x = min(p1.x, p2.x).toDouble()
            val y = min(p1.y, p2.y).toDouble()
            val w = abs(p1.x - p2.x).toDouble()
            val h = abs(p1.y - p2.y).toDouble()

            val tileArea = (abs(p1.x - p2.x) + 1).toLong() * (abs(p1.y - p2.y) + 1).toLong()

            if (tileArea <= maxArea) continue

            val eps = 0.1
            if (w > 0 && h > 0) {
                if (polygon.contains(x + eps, y + eps, w - 2 * eps, h - 2 * eps)) {
                    maxArea = tileArea
                }
            } else {
                if (polygon.contains(x + w / 2.0, y + h / 2.0)) {
                    maxArea = tileArea
                }
            }
        }
    }
    println("$maxArea")
}

fun visualizeSwing(shape: Shape) {
    SwingUtilities.invokeLater {
        val frame = JFrame("Polygon Visualization")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 800)

        frame.add(object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2d = g as Graphics2D

                val bounds = shape.bounds2D

                val padding = 50.0
                val scaleX = (width - 2 * padding) / bounds.width
                val scaleY = (height - 2 * padding) / bounds.height
                val scale = minOf(scaleX, scaleY)

                val transform = AffineTransform()
                transform.translate(padding, padding)
                transform.scale(scale, scale)
                transform.translate(-bounds.x, -bounds.y)

                val transformedShape = transform.createTransformedShape(shape)

                g2d.color = Color.BLACK
                g2d.fill(transformedShape)

                g2d.color = Color.RED
                g2d.draw(transformedShape)
            }
        })

        frame.isVisible = true
    }
}

suspend fun main() {
    measureTimeMillis {
        part2()
    }.let { println("Took $it ms") }
}
