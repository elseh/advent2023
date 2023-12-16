package adv2023

import expect
import println
import readInput

fun main() {
    fun next(d: Int, row: Int, col: Int): List<Int> {
        val change = (d and 1) * 2 - 1
        return if (d < 2) listOf(row, col - change, d) else listOf(row - change, col, d)
    }

    /*
         3
        1, 0,
         2
         */

    next(0, 0, 0).expect(listOf(0, 1, 0))
    next(1, 0, 0).expect(listOf(0, -1, 1))
    next(2, 0, 0).expect(listOf(1, 0, 2))
    next(3, 0, 0).expect(listOf(-1, 0, 3))

    fun next(maze:List<String>, direction:Array<Array<Int>>, row: Int, col: Int, dir:Int): List<List<Int>> {
        if (row !in direction.indices || col !in direction[0].indices) return emptyList()
        if ((1 shl (dir + 1)) and direction[row][col] != 0) return emptyList()
        direction[row][col] = direction[row][col] or (1 shl (dir+1))
        return when (maze[row][col]) {
            '-' -> if (dir >= 2) listOf(0, 1) else listOf(dir)
            '|' -> if (dir < 2) listOf(2, 3) else listOf(dir)
            '/' -> listOf( 3 - dir)
            '\\' -> listOf( dir xor 2)
            else ->  listOf( dir) // .
        }.map { d -> next(d, row, col) }
    }

    fun fire(input: List<String>, state:List<Int>): Int {
        val directions = Array(input.size) { _ -> Array(input[0].length) { _ -> 0 } }
        val queue = ArrayDeque<List<Int>>()
        queue.add(state)
        queue.forEach { (row, col, dir) -> queue.addAll(next(input, directions, row, col, dir))}
        //directions.forEachIndexed { i, line -> line.joinToString("") { if (it != 0) "#" else "." }.println("$i") }
        return directions.sumOf { line -> line.count { it != 0 } }
    }

    fun part1(input: List<String>): Int {
        return fire(input, listOf(0, 0, 0))
    }

    fun part2(input: List<String>): Int {
        val lastH = input[0].length-1
        val lastV = input.size
        return (input.indices.flatMap { listOf(listOf(it, 0, 0), listOf(it, lastH, 1)) } +
                input[0].indices.flatMap { listOf(listOf(0, it, 2), listOf(lastV, it, 3)) })
            .maxOf { fire(input, it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day16_test")
    part1(testInput).expect(46).println()
    part2(testInput).expect(51).println()
    val input = readInput("adv2023/Day16")
    part1(input).println("Part1")
    part2(input).println("Part2")
}
