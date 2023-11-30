package adv2022

import expect
import println
import readInput

fun main() {
    val format = """(\d+)-(\d+),(\d+)-(\d+)""".toRegex()
    fun parseInput(input: List<String>) = input
        .map { (format.find(it)!!.destructured) }
        .map { d -> d.toList().map { it.toInt() } }

    fun part1(input: List<String>): Int {
        return parseInput(input)
            .count { (f1, t1, f2, t2) ->
                (f1 <= f2 && t1 >= t2) || (f2 <= f1 && t2 >= t1)
            }
    }

    fun part2(input: List<String>): Int {
        return parseInput(input)
            .count { (f1, t1, f2, t2) -> !((t1 < f2) || (t2 < f1)) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2022/Day04_test")
    part1(testInput).expect(2).println()
    part2(testInput).expect(4).println()
    val input = readInput("adv2022/Day04")
    part1(input).expect(494).println("Part1")
    part2(input).println("Part2")
}
