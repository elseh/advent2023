package adv2023

import expect
import println
import readInput

fun main() {
    fun locateMirror(input: List<Int>, smudge:Int):Int {
        for (m in 0 until input.size-1 ) {
            val all = (0 .. m)
                .map { m - it to m + 1 + it }
                .filter { it.second < input.size }
                .map { input[it.first] xor  input[it.second] }
                .sumOf { it.countOneBits() }
            if (all == smudge) return m + 1
        }
        return 0
    }

    fun isHash(it: Char) = if (it == '#') 1 else 0

    fun toBinary(a: Int, r: Int) = (a shl 1) + r

    fun solve(input: List<String>, sm:Int): Int {
        var list = input
        var sum = 0
        while (list.isNotEmpty()) {
            val maze = list.takeWhile { it.isNotEmpty() }.map { line -> line.map(::isHash) }
            list = list.drop(maze.size+1)
            val hor = maze
                .map { it.reduce(::toBinary)}
                .let { locateMirror(it, sm) }
            val ver = maze
                .reduce {acc, row -> acc.zip(row, ::toBinary) }
                .let { locateMirror(it, sm) }
            sum += hor*100 + ver
        }
        return sum
    }

    fun part1(input: List<String>): Int {
        return solve(input, 0)
    }

    fun part2(input: List<String>): Int {
        return solve(input, 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day13_test")
    part1(testInput).expect(405).println()
    part2(testInput).expect(400).println()
    val input = readInput("adv2023/Day13")
    part1(input).println("Part1")
    part2(input).println("Part2")
}

