package adv2023

import expect
import println
import readInput

fun main() {
    fun tilt(line: List<Char>): List<Char> {
        val moved = line
            .runningFoldIndexed(-1 to 0) { i, (_, acc), v ->
                when (v) {
                    'O' -> acc to acc + 1
                    '.' -> -1 to acc
                    '#' -> -1 to i + 1
                    else -> -1 to -1
                }
            }.map { it.first }.filter { it >= 0 }
        return line.map {
            when (it) {
                '#' -> '#'
                else -> '.'
            }
        }.toMutableList()
            .also { moved.forEach { pos -> it[pos] = 'O' } }
    }

    fun measure(input: List<List<Char>>): Int {
        return input.reversed().mapIndexed { i, line ->
            line.count { it == 'O' } * (i + 1)
        }.sum()
    }

    fun transform(column: List<Char>, reverse: Boolean) =
        if (reverse) tilt(column.reversed()).reversed() else tilt(column)

    fun tiltVertical(input: List<MutableList<Char>>, reverse: Boolean) {

        input[0].indices
            .map { input.map { line -> line[it] } }
            .forEachIndexed { i, column ->
                transform(column, reverse).forEachIndexed { j, c -> input[j][i] = c }
            }
    }

    fun tiltHorizontal(input: List<MutableList<Char>>, reverse: Boolean) {
        input.forEach { line -> transform(line, reverse).forEachIndexed { i, c -> line[i] = c } }
    }

    fun part1(input: List<String>): Int {
        val map = input.map { it.toMutableList() }
        tiltVertical(map, false)
        return measure(map)
    }

    fun shake(state: List<MutableList<Char>>) {
        tiltVertical(state, false)
        tiltHorizontal(state, false)
        tiltVertical(state, true)
        tiltHorizontal(state, true)
    }

    fun part2(input: List<String>): Int {
        val state = input.map { it.toMutableList() }
        val map: MutableMap<String, Int> = HashMap()
        var step = 0
        val limit = 1000000000
        while (step < limit) {
            shake(state)
            step++
            val desc = state.toString()
            map[desc]?.let { prev ->
                val cycle = step - prev
                val rounds: Int = (limit - step) / cycle
                step += rounds * cycle
            }
            map[desc] = step
        }
        return measure(state)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day14_test")
    part1(testInput).expect(136).println()
    part2(testInput).expect(64).println()
    val input = readInput("adv2023/Day14")
    part1(input).println("Part1")
    part2(input).println("Part2")
}
