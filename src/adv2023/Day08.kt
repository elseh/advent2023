package adv2023

import expect
import println
import readInput

fun main() {
    fun parseGraph(input: List<String>): Map<String, Pair<String, String>> {
        return input.dropWhile { !it.contains("=") }
            .map { it.split(" = (", ", ") }
            .associate { (a, b, c) -> a to (b to c.dropLast(1)) }
    }

    fun walk(graph: Map<String, Pair<String, String>>, tips: String, start:String): List<String> {
        return tips.runningFold(start) {a, ch -> if (ch == 'L') graph[a]!!.first else graph[a]!!.second}.drop(1)
    }

    data class LongPath(val startPos:String,
        val startLength:Int, val cycle:Int, val startExits:Set<Int>, val cycleExits:Set<Int>)

    fun extractThread(pos: String, graph: Map<String, Pair<String, Set<Int>>>, tipLength:Int): LongPath {
        val visited = mutableMapOf<String, Int>()
        var current = pos
        var count = 0
        while (!visited.contains(current)) {
            visited[current] = count ++
            current = graph[current]!!.first
        }

        val startCount = visited[current]!!
        val cycleCount = count - startCount
        val startExits = visited
            .filter { it.value < startCount }
            .flatMap { (pos, num) ->
                graph[pos]!!.second.map { num * tipLength + it }
            }.toSet()
        val cycleExits = visited
            .filter { it.value >= startCount }
            .flatMap { (pos, num) ->
                graph[pos]!!.second
                    .map { (num - startCount) * tipLength + it }
            }.toSet()
        return LongPath(pos, startCount, cycleCount, startExits, cycleExits)
    }

    fun part1(input: List<String>): Int {
        val tips = input[0]
        val graph = parseGraph(input)
        var count = 0
        var start = "AAA"
        while (true) {
            for (step in walk(graph, tips, start)) {
                count++
                if (step == "ZZZ") return count
                start = step
            }
        }
    }

    // skipping possibility to stop on preparation stage
    fun part2(input: List<String>): Long {
        val graph = parseGraph(input)
        val tips = input[0]

        // pos to the end of sequence and indexes of ends in between
        val singleStep: Map<String, Pair<String, Set<Int>>> = graph.map { it.key }
            .associateWith { start ->
                val sequence = walk(graph, tips, start)
                val last = sequence.last()
                val zds = sequence.mapIndexed { i, pos -> i to pos }
                    .filter { it.second.endsWith("Z") }
                    .map { it.first+1 }.toSet()
                (last to zds)
            }

        // fortunately all cycle length were different prime numbers (if not - LCD)
        // another interesting detail - exit was on the end of each cycle (probably it is covered now)
        // third detail - in case of several exits - there should be several answers and a minimum taken
        val length = tips.length

        val (value, _) = graph.filterKeys { it.endsWith("A") }
            .map { extractThread(it.key, singleStep, length).println() }
            .fold(0L to 1L) { (v, mod), path ->
                val found = (1..path.cycle).find { value ->
                    val position = (mod * value + v - path.startLength)
                    val p = if (position <= 0) position + path.cycle else position
                    path.cycleExits.contains((p).mod(path.cycle) * length)
                } ?: 0
                ((v + mod * found) to (mod * path.cycle)).println()
            }
        return value * length
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("adv2023/Day08_test1")
    part1(testInput1).expect(2).println()
    val testInput2 = readInput("adv2023/Day08_test2")
    part1(testInput2).expect(6).println()
    val testInput3 = readInput("adv2023/Day08_test3")
    val input = readInput("adv2023/Day08")
    part2(testInput3).expect(6L).println()

    part1(input).println("Part1")
    part2(input).println("Part2")
}

