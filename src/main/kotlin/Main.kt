import optimized8.solveFinal
import java.io.File

fun main(args: Array<String>) {
    val file = File(args[0])
    solveFinal(file.readLines())
}