package day1

import util.readBytes


const val right = 'R'.code.toByte()
const val left = 'L'.code.toByte()

const val shortRight: Short = 1
const val shortLeft: Short = -1
fun part1() {
    var code: Short = 0 // 1 - right, -1 - left, 0 - straight
    var number = 0
    var sum = 50
    var result = 0
    readBytes(object {}.javaClass.getResource("/input.txt")!!.path) { byte ->
        if (byte == '\n'.code.toByte()) {
            if (code == shortRight) sum += number
            else if (code == shortLeft) sum -= number

            if (sum >= 100) sum %= 100
            if (sum < 0) sum = sum.mod(100)

            code = 0
            number = 0

            if (sum == 0) result++
        } else {
            if (byte == right) {
                code = 1
            } else if (byte == left) {
                code = -1
            } else {
                number = if (number == 0) {
                    (byte - 48)
                } else {
                    number * 10 + (byte - 48)
                }
            }
        }
    }
    println(result)

}

const val nL = '\n'.code.toByte()

inline fun part2() {
    var code: Short = 0
    var number = 0
    var position = 50
    var result = 0

    readBytes("src/main/resources/input3.txt") { byte ->
        if (byte == nL) {
            val rotation = if (code == shortLeft) -number else number
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
            if (byte == right) {
                code = 1
            } else if (byte == left) {
                code = -1
            } else {
                number = if (number == 0) {
                    (byte - 48)
                } else {
                    number * 10 + (byte - 48)
                }
            }
        }
    }
    println(result)
}

fun main() = part2()