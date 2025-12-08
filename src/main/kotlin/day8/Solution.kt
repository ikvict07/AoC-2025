package day8

import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

private data class Point3D(val id: Int, val x: Int, val y: Int, val z: Int)

private data class Edge(val u: Int, val v: Int, val dist: Double) : Comparable<Edge> {
    override fun compareTo(other: Edge): Int = this.dist.compareTo(other.dist)
}

private class DSU(val size: Int) {
    private val parent = IntArray(size) { it }

    private val componentSize = IntArray(size) { 1 }

    fun find(i: Int): Int {
        if (parent[i] != i) {
            parent[i] = find(parent[i])
        }
        return parent[i]
    }

    fun union(i: Int, j: Int): Boolean {
        val rootI = find(i)
        val rootJ = find(j)

        if (rootI != rootJ) {
            parent[rootI] = rootJ
            componentSize[rootJ] += componentSize[rootI]
            return true
        }
        return false
    }

    fun getComponentSizes(): List<Int> {
        val sizes = mutableListOf<Int>()
        val distinctRoots = parent.indices.map { find(it) }.distinct()
        for (root in distinctRoots) {
            sizes.add(componentSize[root])
        }
        return sizes
    }
}

fun solve(input: List<String>) {
    val points = input.mapIndexed { index, line ->
        val (x, y, z) = line.split(",").map { it.trim().toInt() }
        Point3D(index, x, y, z)
    }

    val edges = ArrayList<Edge>()
    for (i in 0 until points.size) {
        for (j in i + 1 until points.size) {
            val p1 = points[i]
            val p2 = points[j]
            val dist = sqrt(
                (p1.x - p2.x).toDouble().pow(2) +
                        (p1.y - p2.y).toDouble().pow(2) +
                        (p1.z - p2.z).toDouble().pow(2)
            )
            edges.add(Edge(i, j, dist))
        }
    }

    edges.sort()

    val dsu = DSU(points.size)

    val limit = 1000
    for (i in 0 until minOf(limit, edges.size)) {
        val edge = edges[i]
        dsu.union(edge.u, edge.v)
    }

    val sizes = dsu.getComponentSizes().sortedDescending()

    if (sizes.size >= 3) {
        val result = sizes[0].toLong() * sizes[1].toLong() * sizes[2].toLong()
        println("$result")
    }
}

fun part1() {
    solve(File("src/main/resources/day8/input.txt").readLines())
}

fun solve2(input: List<String>) {
    val points = input.mapIndexed { index, line ->
        val (x, y, z) = line.split(",").map { it.trim().toInt() }
        Point3D(index, x, y, z)
    }

    val edges = ArrayList<Edge>()
    for (i in 0 until points.size) {
        for (j in i + 1 until points.size) {
            val p1 = points[i]
            val p2 = points[j]
            val dist = sqrt(
                (p1.x - p2.x).toDouble().pow(2) +
                        (p1.y - p2.y).toDouble().pow(2) +
                        (p1.z - p2.z).toDouble().pow(2)
            )
            edges.add(Edge(i, j, dist))
        }
    }

    edges.sort()

    val dsu = DSU(points.size)

    var i = 0
    while (dsu.getComponentSizes().size > 2) {
        val edge = edges[i++]
        dsu.union(edge.u, edge.v)
    }

    var bridgeEdge: Edge? = null

    while (i < edges.size) {
        val edge = edges[i]

        if (dsu.find(edge.u) != dsu.find(edge.v)) {
            bridgeEdge = edge
            break
        }
        i++
    }

    if (bridgeEdge != null) {
        val p1 = points[bridgeEdge.u]
        val p2 = points[bridgeEdge.v]
        println("Result is: ${p1.x * p2.x}")

    }

}

fun part2() {
    solve2(File("src/main/resources/day8/input.txt").readLines())
}

fun main() {
    part2()
}