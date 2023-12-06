package adv2023

import expect
import println
import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Conversion(val startA:Long, val startB:Long, val len:Long) {
        fun applicable(a:Long):Boolean {
            return a >= startA && a < startA + len
        }

        fun convert(a: Long): Long {
            return a + startB - startA
        }

        fun rangeApplicable(start: Long, end: Long): Boolean {
            return (startA in start until end) || (start >= startA && start < startA + len)
        }

        fun convertRange(start: Long, end: Long): List<List<Long>> {
            val start1 = max(start, startA)
            val end1 = min(end, startA + len)
            val result = mutableListOf<List<Long>>()
            if (start < start1) {
                result.add(listOf(start, start1))
            }
            result.add(listOf(start1, end1).map { convert(it) })
            if (end > end1) {
                result.add(listOf(end1, end))
            } else {
                result.add(emptyList())
            }
            return result
        }
    }

    fun parseRules(input:List<String>) = sequence {
        var lines = input.dropWhile { it.isNotEmpty() }.drop(1)
        while (lines.isNotEmpty()) {
            val section = lines.takeWhile { it.isNotEmpty() }
            yield(section.drop(1)
                .map { line -> line.split(" ").map { it.toLong() } }
                .map { (a,b,c) ->  Conversion(b, a, c) }
                .sortedBy { it.startA }
            )
            lines = lines.drop(section.size + 1)
        }
    }

    fun convertList(input: List<Long>, rules: List<Conversion>):List<Long> {
        var rulesLeft = rules
        val result = mutableListOf<Long>()
        for (value in input) {
            rulesLeft = rulesLeft.dropWhile { it.startA + it.len <= value }
            result.add(rulesLeft.takeWhile { it.applicable(value) }
                .fold(value) { acc, it -> it.convert(acc)})
        }
        return result
    }

    fun convertRanges(input: List<List<Long>>, rules: List<Conversion>): List<List<Long>> {
        var rulesLeft = rules
        val result = mutableListOf<List<Long>>()
        for (range in input) {
            rulesLeft = rulesLeft.dropWhile { it.startA + it.len <= range[0] }
            val last = rulesLeft.takeWhile { it.rangeApplicable(range[0], range[1]) }
                .fold(range) { acc, it ->
                    val converted = it.convertRange(acc[0], acc[1])
                    result.addAll(converted.dropLast(1))
                    converted.last()
                }
            if (last.isNotEmpty()) result.add(last)
        }
        return result
    }

    fun part1(input: List<String>): Long {
        val values = input[0].substringAfter(": ").split(" ")
            .map { it.toLong() }.sorted()
        return parseRules(input)
            .fold(values) { list, it -> convertList(list, it).sorted() }[0]
    }

    fun part2(input: List<String>): Long {
        val values = input[0].substringAfter(": ")
            .split(" ")
            .map { it.toLong() }
            .chunked(2)
            .map { (a, len) -> listOf(a, a + len) }
            .sortedBy { it[0] }
        return parseRules(input)
            .fold(values) { list, it -> convertRanges(list, it).sortedBy { it[0] } }[0][0]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day05_test")
    part1(testInput).expect(35L).println()
    part2(testInput).expect(46L).println()
    val input = readInput("adv2023/Day05")
    part1(input).println("Part1")
    part2(input).println("Part2")
}
