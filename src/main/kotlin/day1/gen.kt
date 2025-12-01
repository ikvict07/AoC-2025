package day1

import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.concurrent.ThreadLocalRandom

fun main() {
    val count = 100_000_000
    val fileName = "input4.txt"

    val numbersCache = Array(101) { it.toString().toByteArray() }

    val R = 'R'.code.toByte()
    val L = 'L'.code.toByte()
    val NL = '\n'.code.toByte()

    println("Start generating...")
    val start = System.currentTimeMillis()

    FileOutputStream(fileName).use { fos ->
        BufferedOutputStream(fos, 8 * 1024 * 1024).use { out ->
            val rng = ThreadLocalRandom.current()

            for (i in 0 until count) {
                if (rng.nextBoolean()) out.write(R.toInt()) else out.write(L.toInt())

                val num = rng.nextInt(1, 101)
                out.write(numbersCache[num])

                out.write(NL.toInt())
            }
        }
    }

    val end = System.currentTimeMillis()
    println("Done in ${end - start} ms")
}