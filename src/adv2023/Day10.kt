package adv2023

import expect
import println
import readInput

fun main() {

    //   1
    // 8   2
    //   4

     val NORTH = 1
     val SOUTH = 4
     val WEST = 8
     val EAST = 2
    val bends = mapOf(
        '|' to NORTH + SOUTH,
        '-' to EAST + WEST,
        'L' to NORTH + EAST,
        'J' to NORTH + WEST,
        '7' to WEST + SOUTH,
        'F' to EAST + SOUTH,
        '.' to 0,
        'S' to 15)

    fun reverse(dir:Int):Int {
        return if (dir > 3) dir.shr(2) else dir.shl(2)
    }

    fun go(pos: Pair<Int, Int>, dir: Int, directions: List<List<Int>>): Pair<Pair<Int, Int>, Int> {
        if (pos.first < 0 || pos.second < 0 || pos.first >= directions.size || pos.second >= directions[0].size)
            return pos to 0
        var row = pos.first
        var col = pos.second
        val rev = reverse(dir)
        when (dir) {
            NORTH -> row--
            EAST -> col++
            SOUTH -> row++
            WEST -> col--
        }
        val position = row to col
        if (row !in directions.indices || col !in directions[0].indices) return position to 0
        val d = directions[row][col]
        if (d.and(rev) == 0) return position to 0
        if (d == 15) return position to 15
        return position to d - rev
    }

    fun measure(pos: Pair<Int, Int>, dir: Int, directions: List<List<Int>>) : List<Pair<Pair<Int, Int>, Int>> {
        var state = pos to dir
        val result = mutableListOf<Pair<Pair<Int, Int>, Int>>()
        while (state.second != 0 && state.second != 15) {
            state = go(state.first, state.second, directions)
            if (state.second == 15)
                result.add(state.first to (dir + reverse(result.last().second)))
            else result.add(state)
        }
        return if (state.second == 15) result else listOf()
    }

    fun part1(input: List<String>): Int {
        val directions = input.map { line -> line.map { bends[it]!! } }
        val row = input.indexOfFirst { it.contains('S') }
        val col = input[row].indexOfFirst { it == 'S' }
        val max = listOf(NORTH, WEST, EAST, SOUTH)
            .maxOf { measure(row to col, it, directions).size }

        return max / 2
    }

    fun plot(dots: List<Pair<Pair<Int, Int>, Int>>, rows:Int, cols:Int, directions: List<List<Int>>): List<MutableList<Int>> {
        println("plotting $rows, $cols")
        val result = (1..rows).map { _ -> (1..cols).map { _ -> 0 }.toMutableList() }
        dots.forEach {result[it.first.first][it.first.second] = directions[it.first.first][it.first.second]}
        return result
    }

    fun part2(input: List<String>): Int {
        val directions = input.map { line -> line.map { bends[it]!! } }
        val row = input.indexOfFirst { it.contains('S') }
        val col = input[row].indexOfFirst { it == 'S' }
        val valid = listOf(NORTH, WEST, EAST, SOUTH)
            .map { measure(row to col, it, directions) }
            .find { it.isNotEmpty() }!!
        val plot = plot(valid, directions.size, directions[0].size, directions)
        val (pair, dir) = valid.last()
        plot[pair.first][pair.second] = dir
        return plot.sumOf {
            it.fold(Triple(0, 0, 0)) { (count, zone, corner), num ->
                if (num == 0) return@fold Triple(count + zone, zone, corner)
                when (val pipeType = num.and(NORTH + SOUTH)) {
                    0 -> Triple(count, zone, corner)
                    NORTH + SOUTH -> Triple(count, 1 - zone, corner)
                    else -> {
                        when (corner) {
                            0 -> Triple(count, zone, pipeType)
                            pipeType -> Triple(count, zone, 0)// North or South
                            else -> Triple(count, 1 - zone, 0)
                        }
                    }
                }
            }.first
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day10_test")
    part1(testInput).expect(8).println()
    part2(readInput("adv2023/Day10_test1")).expect(4).println()
    part2(readInput("adv2023/Day10_test2")).expect(8).println()
    val input = readInput("adv2023/Day10")
    part1(input).println("Part1")
    part2(input).println("Part2")
}

