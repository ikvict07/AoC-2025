package optimized8

import java.io.File
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction

private class PrimitiveDSU(size: Int) {
    private val parent = IntArray(size) { it }
    private val rank = IntArray(size) { 0 }

    var numComponents = size
        private set

    fun find(i: Int): Int {
        var root = i
        while (root != parent[root]) root = parent[root]

        var curr = i
        while (curr != root) {
            val next = parent[curr]
            parent[curr] = root
            curr = next
        }
        return root
    }

    fun union(i: Int, j: Int): Boolean {
        val rootI = find(i)
        val rootJ = find(j)

        if (rootI != rootJ) {
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI
            } else {
                parent[rootJ] = rootI
                rank[rootI]++
            }
            numComponents--
            return true
        }
        return false
    }
}

fun solveFinal(input: List<String>) {
    val n = input.size

    val px = IntArray(n)
    val py = IntArray(n)
    val pz = IntArray(n)

    for (i in 0 until n) {
        val line = input[i]
        val p1 = line.indexOf(',')
        val p2 = line.indexOf(',', p1 + 1)

        px[i] = line.substring(0, p1).trim().toInt()
        py[i] = line.substring(p1 + 1, p2).trim().toInt()
        pz[i] = line.substring(p2 + 1).trim().toInt()
    }

    val maxEdges = n * (n - 1) / 2
    val edgeU = IntArray(maxEdges)
    val edgeV = IntArray(maxEdges)
    val edgeDistSq = LongArray(maxEdges)
    val edgeIndices = IntArray(maxEdges)

    var k = 0
    for (i in 0 until n) {
        val x1 = px[i];
        val y1 = py[i];
        val z1 = pz[i]
        for (j in i + 1 until n) {
            val dx = (x1 - px[j]).toLong()
            val dy = (y1 - py[j]).toLong()
            val dz = (z1 - pz[j]).toLong()

            edgeDistSq[k] = dx * dx + dy * dy + dz * dz
            edgeU[k] = i
            edgeV[k] = j
            edgeIndices[k] = k
            k++
        }
    }

    parallelQuickSort(edgeIndices, edgeDistSq, 0, k - 1)

    val dsu = PrimitiveDSU(n)
    var ptr = 0

    while (dsu.numComponents > 2 && ptr < k) {
        val idx = edgeIndices[ptr]
        dsu.union(edgeU[idx], edgeV[idx])
        ptr++
    }

    while (ptr < k) {
        val idx = edgeIndices[ptr]
        val u = edgeU[idx]
        val v = edgeV[idx]

        if (dsu.find(u) != dsu.find(v)) {
            val p1x = px[u].toLong()
            val p2x = px[v].toLong()

            println("${p1x * p2x}")
            break
        }
        ptr++
    }

}

private const val SERIAL_THRESHOLD = 10000

private class ParallelSortAction(
    val indices: IntArray,
    val weights: LongArray,
    val low: Int,
    val high: Int
) : RecursiveAction() {

    override fun compute() {
        if (high - low < SERIAL_THRESHOLD) {
            quickSortSequential(indices, weights, low, high)
        } else {
            val pi = partition(indices, weights, low, high)
            invokeAll(
                ParallelSortAction(indices, weights, low, pi - 1),
                ParallelSortAction(indices, weights, pi + 1, high)
            )
        }
    }
}

private fun quickSortSequential(indices: IntArray, weights: LongArray, low: Int, high: Int) {
    if (low < high) {
        val pi = partition(indices, weights, low, high)
        quickSortSequential(indices, weights, low, pi - 1)
        quickSortSequential(indices, weights, pi + 1, high)
    }
}

private fun partition(indices: IntArray, weights: LongArray, low: Int, high: Int): Int {
    val pivot = weights[indices[high]]
    var i = (low - 1)

    for (j in low until high) {
        if (weights[indices[j]] < pivot) {
            i++
            val temp = indices[i]
            indices[i] = indices[j]
            indices[j] = temp
        }
    }
    val temp = indices[i + 1]
    indices[i + 1] = indices[high]
    indices[high] = temp

    return i + 1
}

fun parallelQuickSort(indices: IntArray, weights: LongArray, low: Int, high: Int) {
    val pool = ForkJoinPool.commonPool()
    pool.invoke(ParallelSortAction(indices, weights, low, high))
}

fun main() {
    val file = File("src/main/resources/day8/input.txt")
    solveFinal(file.readLines())
}