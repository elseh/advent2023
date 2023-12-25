package adv2023

import expect
import println
import readInput
import java.time.Duration
import java.time.Instant

fun main() {
    data class State(val pos: Pair<Int, Int>, val steps: Int, val visited: Long)

    fun isValid(ch: Char?, dir: Int, sand:Boolean) =
        when (ch) {
            '.' -> true
            '#' -> false
            '>' -> dir == Directions.RIGHT || sand
            '<' -> dir == Directions.LEFT || sand
            'v' -> dir == Directions.DOWN || sand
            '^' -> dir == Directions.UP || sand
            else -> false
        }

    fun get(p:Pair<Int, Int>, maze:List<String>):Char? {
        if (p.second !in maze.indices || p.first !in maze[0].indices) return null
        return maze[p.second][p.first]
    }

    // walks from position to the next intersection
    fun walk(initPosition: Pair<Int, Int>, dir:Int, maze:List<String>, sand:Boolean):Pair<Pair<Int, Int>, Int>? {
        var state = Directions.applyStep(initPosition, dir to 1) to dir
        var count = 1
        if (!isValid(get(state.first, maze), state.second, sand)) return null
        while (true) {
            val available = (0..3).filter { it xor 1 != state.second }
                .map { Directions.applyStep(state.first, it to 1) to it }
                .filter { (pos, direction) -> isValid(get(pos, maze), direction, sand) }
            if (available.size == 1) {
                state = available.first()
                count ++
            } else {
                return state.first to count
            }
        }
    }
    data class Edge(val from: Pair<Int, Int>, val to: Pair<Int, Int>, val len:Int)

    fun buildGraph(input: List<String>, sand:Boolean) : Map<Pair<Int, Int>, List<Edge>> {
        val start = input.first().indexOfFirst { it != '#' }
        val q = ArrayDeque<Pair<Int, Int>>()
        val set = mutableSetOf<Pair<Int, Int>>()
        val list = mutableListOf<Edge>()
        q.add(Pair(start, 0))
        while (q.isNotEmpty()) {
            val pos = q.removeFirst()
            if (set.contains(pos)) continue
            set.add(pos)
            (0..3).mapNotNull { walk(pos, it, input, sand) }
                .map { Edge(pos, it.first, it.second) }
                .forEach {
                    list.add(it)
                    q.add(it.to) }
        }
        return list.groupBy { it.from }
    }

    fun MutableMap<Int, Long>.build(p: Pair<Int, Int>):Long {
        val hash = p.first * 256 + p.second
        if (!this.containsKey(hash)) {
            this[hash] = 1L shl this.size
        }
        return this[hash]!!
    }

    fun solve(input: List<String>, sand: Boolean): Int {
        val graph = buildGraph(input, sand)
        val q = ArrayDeque<State>()
        val map = mutableMapOf<Pair<Int, Int>, Int>()
        val start = input.first().indexOfFirst { it != '#' }
        val end = input.last().indexOfFirst { it != '#' }
        val locationMap = mutableMapOf<Int, Long>()
        fun compute(p: Pair<Int, Int>) = locationMap.build(p)

        val p = start to 0
        q.add(State(p, 0, compute(p)))
        var iterations = 0L
        while (q.isNotEmpty()) {
            val state = q.removeFirst()
            val best = map[state.pos] ?: -1
            iterations++
            if (best < state.steps) map[state.pos] = state.steps else if (!sand) continue
            graph[state.pos]!!.filter {
                state.visited and compute(it.to) == 0L }
                .map { edge -> State(edge.to, state.steps + edge.len, state.visited + compute(edge.to)) }
                .forEach { q.addFirst(it) }
        }
        iterations.println("iterations")
        return map[(end to input.lastIndex)] ?: -1
    }

    fun part1(input: List<String>): Int {
        return solve(input, false)
    }

    fun part2(input: List<String>): Int {
        return solve(input, true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day23_test")
    val input = readInput("adv2023/Day23")
    part1(testInput).expect(94).println()
    part1(input).println("Part1")
    part2(testInput).expect(154).println()
    val timeStart = Instant.now()
    part2(input).println("Part2")
    Duration.between(timeStart, Instant.now()).seconds.println("time passed")
}