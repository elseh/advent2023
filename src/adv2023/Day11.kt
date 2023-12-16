package adv2023

import expect
import println
import readInput
fun main() {

    fun parseInput(input: List<String>):List<Pair<Int, Int>> {
        return input.flatMapIndexed { row, line ->
            line.mapIndexed { col, c -> (row to col) to c }
        }
            .filter { it.second == '#' }
            .map { it.first }
    }

    fun sum(input: List<Int>, width: Int, multiple:Int) : Long {
        val plot = (1 .. width).map {_ -> 0}.toMutableList()
        input.forEach{ plot[it]++}
        return plot.fold(listOf(0, 0L, 0L)) { (count, sum, result), value ->
            val m = if (value == 0) multiple else 1
            listOf(count+value, sum + m * count, result + (sum + count) * value)
        }[2]
    }

    fun solve(input: List<String>, mult: Int): Long {
        val pairs = parseInput(input)
        return sum(pairs.map { it.first }, input.size, mult) +
                sum(pairs.map { it.second }, input[0].length, mult)
    }

    fun part1(input: List<String>): Long {
        return solve(input, 2)
    }


    fun part2(input: List<String>): Long {
        return solve(input, 1000000)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day11_test")
    part1(testInput).expect(374L).println()
    solve(testInput, 10).expect(1030L).println()
    solve(testInput, 100).expect(8410L).println()
    val input = readInput("adv2023/Day11")
    part1(input).println("Part1")
    part2(input).println("Part2")
}

