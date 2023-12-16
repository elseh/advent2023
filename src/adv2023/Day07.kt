package adv2023

import expect
import println
import readInput

fun main() {
    val map = mapOf(
        'A' to 14,
        'K' to 13,
        'Q' to 12,
        'J' to 11,
        'T' to 10,
        'N' to 0,
    )
    data class Hand(val hand:Int, val type:Int, val bet:Int, val base: String)

    fun computeType(hand: String):Int {
        val counts = hand.asSequence().groupBy { it }
            .map { it.value.size }
            .sorted()
        return counts.reduceRight { value, acc -> acc * 10 + value } *
                (counts.size until 5).fold(1) { acc, _ -> acc * 10 }
    }

    fun computeType2(hand: String):Int {
        val best = hand.filter { it != 'J' }
            .groupBy { it }
            .maxByOrNull { (_, v) -> v.size }?.key ?: 'N'
        return computeType(hand.replace('J', best))
    }

    fun handToInt(hand:String) : Int {
        return hand.map {ch ->  map.getOrElse(ch) { ch.digitToInt() } }
            .reduce {acc, value -> acc * 16 + value}
    }

    fun handsOrder(input: List<String>, handConstructor: (List<String>)-> Hand): Long {
        return input.asSequence().map { it.split(" ") }
            .map(handConstructor)
            .sortedWith ( compareBy({ it.type}, {it.hand }))
            .mapIndexed { i, h -> (i + 1L) * h.bet }
            .sum()
    }

    fun part1(input: List<String>): Long {
        return handsOrder(input) { (hand, bet) ->
            Hand(handToInt(hand), computeType(hand), bet.toInt(), hand) }

    }

    fun part2(input: List<String>): Long {
        return handsOrder(input) { (hand, bet) ->
            Hand(handToInt(hand.replace('J', 'N')), computeType2(hand), bet.toInt(), hand) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day07_test")
    part1(testInput).expect(6440L).println()
    part2(testInput).expect(5905L).println()
    val input = readInput("adv2023/Day07")
    part1(input).println("Part1")
    part2(input).println("Part2")

}


