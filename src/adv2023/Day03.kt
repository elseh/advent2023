package adv2023

import expect
import println
import readInput

fun main() {
    data class Num(val row: Int, val start:Int, val end:Int)
    data class Part(val type:Char, val row:Int, val pos:Int)

    fun numbers(line: String, pos: Int) = sequence {
        var last = -1
        for (i in line.indices) {
            if (!line[i].isDigit()) {
                if (last < i - 1)
                    yield(Num(pos, last + 1, i))
                last = i
            }
        }

        if (last < line.length - 1)
            yield(Num(pos, last+1, line.length))
    }

    fun parts(line: String, row: Int) = sequence {
        for (i in line.indices) {
            if (!line[i].isDigit() && line[i] != '.') {
                yield(Part(line[i], row, i))
            }
        }
    }

    fun numbers(input: List<String>): List<Num> {
        return input.flatMapIndexed {pos, line -> numbers(line, pos)}
    }

    fun parts(input: List<String>): List<Part> {
        return input.flatMapIndexed { index, line -> parts(line, index)}
    }

    fun isNear(num: Num, part: Part): Boolean {
        return num.end >= part.pos && num.start <= part.pos + 1
    }

    fun part1(input: List<String>): Int {
        return sequence {
            val numbers = numbers(input)
            var parts = parts(input)
            for (num in numbers) {
                parts = parts.dropWhile { it.row < num.row - 1 }
                val hasPart = parts.takeWhile { it.row <= num.row + 1 }.any { isNear(num, it)}
                if (hasPart) {
                    yield(num)
                }
            }
        }.map { input[it.row].substring(it.start, it.end).toInt() }.sum()
    }

    fun part2(input: List<String>): Int {
        return sequence {
            var numbers = numbers(input)
            val parts = parts(input).filter { it.type == '*' }
            for (part in parts) {
                numbers = numbers.dropWhile { it.row < part.row - 1 }
                yield(numbers.takeWhile { it.row < part.row + 2 }
                    .filter { isNear(it, part) })
            }
        }.filter { it.size == 2 }.map { mapped ->
                mapped.map { num -> input[num.row].substring(num.start, num.end).toInt() }.reduceRight { a, b -> a * b }
            }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day03_test")
    part1(testInput).expect(4361).println()
    part2(testInput).expect(467835).println()
    val input = readInput("adv2023/Day03")
    part1(input).println("Part1")
    part2(input).println("Part2")
}
