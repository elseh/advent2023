package adv2023

import expect
import println
import readInput

fun main() {
   val numMap = mapOf(
       "one" to 1,
       "two" to 2,
       "three" to 3,
       "four" to 4,
       "five" to 5,
       "six" to 6,
       "seven" to 7,
       "eight" to 8,
       "nine" to 9,
       "zero" to 0,
   )

    fun part1(input: List<String>): Int {
        val formats = listOf(
            """^\D*(\d)""".toRegex(),
            """(\d)\D*$""".toRegex())
        return input
            .map { line ->
                formats.map { f ->
                    val (num) = f.find(line)!!.destructured
                    num.toInt()
                }
            }.sumOf { list -> list[0] * 10 + list[1] }
    }

    fun parse(num: String) = numMap.getOrElse(num) { num.toInt() }

    fun part2(input: List<String>): Int {
        val numFormat = numMap.keys.joinToString("|")
        val format = """(\d|$numFormat)""".toRegex()
        val firsts = input
            .map { format.find(it)!!.destructured }
            .map { (num) -> num }
            .sumOf(::parse)
        val revFormat = """(\d|${numFormat.reversed()})""".toRegex()
        val seconds: Int = input
            .map { revFormat.find(it.reversed())!!.destructured }
            .map { (num) -> num.reversed() }
            .sumOf(::parse)
        return firsts * 10 + seconds
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day01_test")
    part1(testInput).expect(142).println()
    part2(readInput("adv2023/Day01_test2")).expect(299).println()
    val input = readInput("adv2023/Day01")
    part1(input).println("Part1")
    part2(input).println("Part2")
}
