package util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

inline fun readBytes(filePath: String, crossinline consumer: (Byte) -> Unit) {
    RandomAccessFile(filePath, "r").use { file ->
        val channel = file.channel
        val fileSize = channel.size()

        val mmap = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize)

        val bufferSize = 8192
        val heapBuffer = ByteArray(bufferSize)

        var ptr = 0L
        while (ptr < fileSize) {
            val remaining = fileSize - ptr
            val len = if (remaining > bufferSize) bufferSize else remaining.toInt()

            mmap.get(heapBuffer, 0, len)

            for (i in 0 until len) {
                consumer(heapBuffer[i])
            }

            ptr += len
        }
    }
}
fun readLinesMmap(filePath: String) = flow {
    RandomAccessFile(filePath, "r").use { file ->
        val channel = file.channel
        val fileSize = channel.size()
        var position = 0L
        val mapSize = 1024L * 1024L * 256L

        val lineBuffer = ByteArrayOutputStream(8192)

        while (position < fileSize) {
            val sizeRemaining = fileSize - position
            val sizeToMap = minOf(mapSize, sizeRemaining)

            val mmap = channel.map(FileChannel.MapMode.READ_ONLY, position, sizeToMap)

            while (mmap.hasRemaining()) {
                val byte = mmap.get()

                if (byte == '\n'.code.toByte()) {
                    val line = lineBuffer.toString("UTF-8")

                    val cleanLine = if (line.endsWith("\r")) line.dropLast(1) else line

                    emit(cleanLine)

                    lineBuffer.reset()
                } else {
                    lineBuffer.write(byte.toInt())
                }
            }

            position += sizeToMap
        }

        if (lineBuffer.size() > 0) {
            val line = lineBuffer.toString("UTF-8")
            val cleanLine = if (line.endsWith("\r")) line.dropLast(1) else line
            emit(cleanLine)
        }
    }
}.flowOn(Dispatchers.IO)

