package adv2023

import adv2023.Directions.DOWN
import adv2023.Directions.LEFT
import adv2023.Directions.RIGHT
import adv2023.Directions.UP
import expect
import println
import readInput
import java.util.PriorityQueue

fun main() {
    data class Pos(val row:Int, val col:Int, val dir:Int) {
        fun turns(): List<Int> {
            return if (dir < 2) listOf(UP, DOWN) else listOf(LEFT , RIGHT)
        }

        fun next(d:Int):Pos {
            return Directions.applyStep(col to row, d to 1)
                .let { (c, r) -> Pos(r, c, d) }
        }
    }

    fun moves(pos:Pos, cost:Int, len: Int, input: List<String>, drop: Int): List<Pair<Pos, Int>> {
        return pos.turns()
            .flatMap { d ->
                (1..len)
                    .runningFold(pos to cost) { (p, c), _ ->
                        val next = p.next(d)
                        if (next.row !in input.indices || next.col !in input[0].indices)
                            next to -1
                        else
                            next to input[next.row][next.col].digitToInt() + c
                    }.drop(1).drop(drop)
            }
            .filter { it.second >= 0 }.also { if (it.size > len * 2) println("ops $pos $it") }
    }



    fun solve(input: List<String>, max: Int, drop: Int): Int {
        val states: MutableMap<Pos, Int> = mutableMapOf()
        val queue = PriorityQueue<Pair<Pos, Int>>(compareBy { it.second })
        queue.offer(Pos(0, 0, DOWN) to 0)
        queue.offer(Pos(0, 0, RIGHT) to 0)
        while (queue.isNotEmpty()) {
            val (pos, cost) = queue.poll()
            if (states.containsKey(pos)) continue
            if (pos.row == input.indices.last && pos.col == input[0].indices.last)
                return cost
            states[pos] = cost
            moves(pos, cost, max, input, drop).forEach { pair -> queue.offer(pair) }
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        return solve(input, 3, 0)
    }

    fun part2(input: List<String>): Int {
        return solve(input, 10, 3)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day17_test")
    part1(testInput).expect(102).println()
    part2(testInput).expect(94).println()
    part2(readInput("adv2023/Day17_test1")).expect(71).println()
    val input = readInput("adv2023/Day17")
    part1(input).println("Part1")
    part2(input).println("Part2")


}
