package adv2023

import expect
import println
import readInput

fun main() {
    fun randomJoin(original: Map<String, List<Pair<String, String>>>):Pair<Int, Int> {
        val graph = original.map { it }.associate { (k, v) -> k to v.toMutableList() }.toMutableMap()
        val weights = graph.keys.associateWith { 1 }.toMutableMap()
        while (graph.size > 2) {
            val (a, b) = graph.values.random().random()
            // joining
            weights[a] = weights[a]!! + weights[b]!!
            weights.remove(b)
            val firstList = graph[a]!!
            firstList.removeAll { it == (a to b) }
            graph[b]!!.forEach { (old, next) ->
                graph[next]!!.remove(next to old)
                if (next != a) {
                    firstList.add(a to next)
                    graph[next]!!.add(next to a)
                }
            }
            graph.remove(b)
        }
        return graph.values.first().size to weights.values.reduce {a,b -> a *b}
    }

    fun part1(input: List<String>): Int {
        val graph = input.map { it.split(": ", " ") }
            .flatMap {
                val first = it.first()
                it.drop(1).flatMap { second -> listOf(first to second, second to first) }
            }
            .groupBy { it.first }
            .map { it}
            .associate { (k, v) -> k to v.toMutableList() }
        while (true) {
            val (edges, result) = randomJoin(graph).println("result")
            if (edges == 3) return result
        }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day25_test")
    val input = readInput("adv2023/Day25")
    part1(testInput).expect(54).println()
    part1(input).println("Part1")
}
