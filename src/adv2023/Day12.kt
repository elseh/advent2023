package adv2023

import expect
import println
import readInput
fun main() {
    fun repeat(line:String, n: Int, separator: Char):String {
        return line + ("$separator$line").repeat(n - 1)
    }

    fun read(line:String, n:Int): Pair<String, String> {
        val (first, second) = line.split(" ")
        val pattern = repeat(second, n, ',').split(",")
            .map { it.toInt() }
            .joinToString(".") { "#".repeat(it) }
        return repeat(first, n, '?')  to pattern
    }

    fun matches(input:String, pattern: String) : Long {
        val g : Array<Long> = Array(pattern.length+2) { _ -> 0}
        g[0] = 1
        for (chI in input) {
            var last = g[0]
            if (chI == '#') g[0] = 0
            var diag = last
            pattern.forEachIndexed { j, chP ->
                last = g[j+1]
                g[j+1] = when (chI) {
                    '.' -> if (chP == chI) diag + last else 0
                    '#' -> if (chP == chI) {diag} else 0
                    '?' -> if (chP == '.') diag + last else diag
                    else -> 0
                }
                diag = last
            }
            last = g[pattern.length+1]
            g[pattern.length+1] = if (chI == '#') 0 else diag + last
        }
        return g.last() + g[pattern.length]
    }

    fun part1(input: List<String>): Long {
        return input.map { read(it, 1) }.sumOf { matches(it.first, it.second) }
    }


    fun part2(input: List<String>): Long {
        return input.map { read(it, 5) }.sumOf { matches(it.first, it.second) }
    }

    // test if implementation meets criteria from the description, like:
    part1(readInput("adv2023/Day12_test0")).expect(6L).println()
    part1(readInput("adv2023/Day12_test1")).expect(21L).println()
    part2(readInput("adv2023/Day12_test1")).expect(525152L).println()
    val input = readInput("adv2023/Day12")
    part1(input).println("Part1")
    part2(input).println("Part2")
}

