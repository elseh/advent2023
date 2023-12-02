package adv2023

import expect
import println
import readInput

fun main() {
    // Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
    val ideal = mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14,
    )
    data class Game(val id: Int, var picks: Map<String, Int>)

    fun parseGame(line: String): Game {
        val description = line.split(": ", "; ", ", ", " ")
        val id = description[1].toInt()
        val map = description.drop(2)
            .chunked(2)
            .groupBy( { it[1]} , { it[0].toInt()})
            .map { entry -> entry.key to entry.value.max() }
            .toMap()
        map.size.expect(3) // what could happen if we have not meet some color?
        return Game(id, map)
    }

    fun checkGame(game : Game) : Boolean {
        return game.picks
            .all { entry -> entry.value <= ideal.getOrDefault(entry.key, 0) }
    }

    fun measureGame(game : Game) : Int {
        return game.picks.values.reduce { a, b -> a * b }
    }

    fun part1(input: List<String>): Int {
        return input
            .map(::parseGame)
            .filter(::checkGame)
            .sumOf { g -> g.id }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { parseGame(it) }
            .sumOf { measureGame(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day02_test")
    part1(testInput).expect(8).println()
    part2(testInput).expect(2286).println()
    val input = readInput("adv2023/Day02")
    part1(input).println("Part1")
    part2(input).println("Part2")
}
