import sun.misc.Unsafe
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import kotlin.jvm.java

val unsafe: Unsafe = try {
    val field = Unsafe::class.java.getDeclaredField("theUnsafe")
    field.isAccessible = true
    field.get(null) as Unsafe
} catch (e: Exception) {
    throw RuntimeException("Unsafe not available", e)
}

const val RIGHT = 'R'.code.toByte()
const val LEFT = 'L'.code.toByte()
const val NEWLINE = '\n'.code.toByte()
const val ZERO = '0'.code.toByte()

fun main(args: Array<String>) {
    val arg = if (args.isEmpty()) "src/main/resources/input4.txt" else args[0]
    val file = File(arg)

    RandomAccessFile(file, "r").use { raf ->
        val channel = raf.channel
        val fileSize = channel.size()

        val mmap = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize)

        val addressField = java.nio.Buffer::class.java.getDeclaredField("address")
        addressField.isAccessible = true
        val startAddress = addressField.getLong(mmap)

        val endAddress = startAddress + fileSize

        var currentAddr = startAddress
        var code = 0
        var number = 0
        var position = 50
        var result = 0

        while (currentAddr < endAddress) {
            val b = unsafe.getByte(currentAddr)
            currentAddr++

            if (b == NEWLINE) {
                val rotation = if (code == -1) -number else number
                var endPosition = position + rotation

                if (endPosition < 0) {
                    if (position == 0) result--
                    while (endPosition < 0) {
                        endPosition += 100
                        result++
                    }
                }
                while (endPosition >= 100) {
                    endPosition -= 100
                    if (endPosition != 0) result++
                }
                if (endPosition == 0) result++

                position = endPosition
                code = 0
                number = 0
            } else {
                if (b == RIGHT) {
                    code = 1
                } else if (b == LEFT) {
                    code = -1
                } else {
                    number = (number * 10) + (b - ZERO)
                }
            }
        }
        println(result)
    }
}