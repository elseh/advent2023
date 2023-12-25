package adv2023

import expect
import println
import readInput
import kotlin.math.abs

fun main() {

    fun computeVolume(steps: List<Pair<Int, Int>>): Long {
        val volume = steps
            .fold((0 to 0) to (0L)) { (pos, state), (d, len) ->
                val next = Directions.applyStep(pos, d to len)
                val vDiff = pos.first.toLong() * (pos.second - next.second)
                next to (state + vDiff)
            }
            .also { it.first.expect(0 to 0) }.second
        val corners = steps
            .fold(0 to steps.last().first) { (sum, last), (d, _) ->
                val clock = Directions.clock(last) == d
                (sum + if (clock) 3 else 1) to d
            }.first / 4
        val perimeter = steps.sumOf { it.second - 1 } / 2
        return abs(volume) + corners + perimeter
    }

    fun parse1(line:String): Pair<Int, Int> {
        val (a, b) = line.split(" ")
        return Directions.dir[a[0]]!! to b.toInt()
    }

    fun part1(input: List<String>): Long {
        val steps = input.map (::parse1)
        return computeVolume(steps)
    }

    val convert2 = listOf('R', 'D', 'L', 'U')
    fun parse2(line: String): Pair<Int, Int> {
        val input = line.substringAfter('#').substringBefore(')')
        return Directions.dir[convert2[input[5].digitToInt()]]!! to
            input.dropLast(1).toInt(radix = 16)

    }
    fun part2(input: List<String>): Long {
        val steps = input.map (::parse2)
        return computeVolume(steps)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day18_test")
    computeVolume(listOf(
        0 to 5, 2 to 2, 1 to 5, 3 to 2
    )).expect(18)
    part1(testInput).expect(62).println()

    val input = readInput("adv2023/Day18")
    part1(input).println("Part1")

    part2(testInput).expect(952408144115).println()
    part2(input).println("Part2")
}
