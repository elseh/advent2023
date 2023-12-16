package adv2023

import expect
import println
import readInput

fun main() {
    fun predict(input: List<Long>, deep:Int = 0) : Long {
        if (input.all { it == 0L }) return 0
        return  input.last() + predict(input.windowed(2) {w -> w[1]- w[0]}, deep+1)
    }

    fun parseInput(input: List<String>) =
        input.map { line -> line.split(" ").map { it.toLong() } }

    fun part1(input: List<String>): Long {
        return parseInput(input).sumOf { predict(it) }
    }

    fun part2(input: List<String>): Long {
        return parseInput(input).sumOf { predict(it.reversed()) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day09_test")
    part1(testInput).expect(114L).println()
    part2(testInput).expect(2L).println()
    val input = readInput("adv2023/Day09")
    part1(input).println("Part1")
    part2(input).println("Part2")
}

