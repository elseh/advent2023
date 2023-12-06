package adv2023

import expect
import println
import readInput

fun main() {
    fun parseList(line : String): List<List<Int>> {
        return line.substringAfter(": ")
            .split(" | ")
            .map { part -> part.split(" ").filter { it.isNotEmpty() }.map { it.toInt() } }
    }

    fun score1(win: List<Int>, has: List<Int>) : Int {
        val winSet = win.toSet()
        return 1.shl(has.count { winSet.contains(it) }).shr(1)
    }

    fun score2(win: List<Int>, has: List<Int>) : Int {
        val winSet = win.toSet()
        return has.count { winSet.contains(it) }
    }

    fun part1(input: List<String>): Int {
        return input
            .map { parseList(it) }.sumOf { score1(it[0], it[1]) }
    }

    fun part2(input: List<String>): Int {
        val cards = MutableList(input.size+1) {_ -> 0}
        input.reversed()
            .map { parseList(it) }
            .map { score2(it[0], it[1]) }
            .forEachIndexed { i, won ->
                val index = i+1
                val added = 1 + cards[i] - cards[i- won]
                cards[index] = cards[i] + added
                cards[index]
            }
        return cards.last()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day04_test")
    part1(testInput).expect(13).println()
    part2(testInput).expect(30).println()
    val input = readInput("adv2023/Day04")
    part1(input).println("Part1")
    part2(input).println("Part2")
}
