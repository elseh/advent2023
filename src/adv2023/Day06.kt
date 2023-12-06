package adv2023

import expect
import println
import readInput
import kotlin.math.*

fun main() {
    fun ways(time: Long, distance: Long): Int {
        val x = sqrt(time * time - 4 * distance - 4.0).toInt()
        return (x + (time.and(1L))).toInt()
    }

    fun extractNumbers(line: String) = line.split("\\W+".toRegex()).drop(1)

    fun part1(input: List<String>): Int {
        val (times, distances) = input.map { line -> extractNumbers(line).map { it.toLong() } }
        return times.zip(distances)
            .map { (t, d) -> ways(t, d) }
            .reduce { a, b -> a * b }
    }

    fun part2(input: List<String>): Int {
        val (times, distances) = input.map { line -> extractNumbers(line).joinToString("").toLong() }
        return ways(times, distances)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day06_test")
    part1(testInput).expect(288).println()
    part2(testInput).expect(71503).println()
    val input = readInput("adv2023/Day06")
    part1(input).println("Part1")
    part2(input).println("Part2")

}


