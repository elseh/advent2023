package adv2023

import expect
import println
import readInput

fun main() {
    fun toHash(s:String):Int {
        // ?? char to ascii? never heard
        return s.fold(0) { acc, v -> ((acc + v.code) * 17) and 255 }
    }
    fun part1(input: List<String>): Int {
        return input[0].split(",").sumOf(::toHash)
    }

    fun part2(input: List<String>): Int {
        return input[0].split(",")
            .map { op ->
                if (op.endsWith('-')) {
                    Triple('-', op.dropLast(1), -1)
                } else {
                    Triple('=', op.substringBefore('='), op.last().digitToInt())
                }
            }
            .groupBy { (_, label, _) -> toHash(label) }
            .map { (boxId, triplets) ->
                val box = mutableListOf<String>()
                val lenses = triplets.groupBy({ (_, label, _) -> label }, { (_, _, focal) -> focal })
                    .map { (key, list) -> key to list.last() }
                    .associate { it }
                triplets.forEach { (action, label) ->
                    if (action == '-') {
                        box.remove(label)
                    } else {
                        if (!box.contains(label)) {
                            box.add(label)
                        }
                    }
                }
                box.mapIndexed { i, label -> (i+1) * lenses[label]!! }.sum() * (boxId + 1)
            }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day15_test")
    part1(testInput).expect(1320).println()
    part2(testInput).expect(145).println()
    val input = readInput("adv2023/Day15")
    part1(input).println("Part1")
    part2(input).println("Part2")
}
