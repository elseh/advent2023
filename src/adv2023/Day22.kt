package adv2023

import expect
import println
import readInput

fun main() {
    data class Brick(var coords:MutableList<IntRange>, var canRemove:Boolean = true,
                     val depends:MutableList<Brick> = mutableListOf())
    {
        fun range(d: Int) = coords[d]

        fun flatCoords():List<Pair<Int, Int>> {
            return range(0).flatMap { x -> range(1).map { y -> x to y } }
        }

        override fun toString():String {
            return coords.map { it.first }.joinToString(",") + "~" +
                coords.map { it.last }.joinToString(",")
        }

    }

    fun shiftTo(range: IntRange, start:Int):IntRange {
        val (oldStart, oldEnd) = range.first to range.last
        return start .. oldEnd - oldStart + start
    }

    fun parseBrick(line:String):Brick {
            // 4,1,154~6,1,154
        val split = line.split(",", "~").map { it.toInt() }
        val c1 = split.take(3)
        val c2 = split.takeLast(3)
        return Brick((c1 zip c2).map { it.first .. it.second }.toMutableList())
    }

    val zeroBrick = parseBrick("0,0,0~0,0,0")
    val z = 2
    fun putOnTop(b: Brick, tops: Array<Array<Brick>>): Brick {
        val coords = b.flatCoords()
        val supportingSet = coords.map { tops[it.first][it.second] }.toSet()
        val maxLevel = supportingSet.maxOf { it.coords[z].last }
        b.coords[z] = shiftTo(b.coords[z], maxLevel+1)
        val dependent = supportingSet.filter { it.coords[z].last() == maxLevel }
        b.depends.addAll(dependent)
        if (dependent.size == 1) dependent[0].canRemove = false

        coords.forEach{tops[it.first][it.second] = b}
        return b
    }

    fun orderBricks(input: List<String>): List<Brick> {
        val bricks = input.map { parseBrick(it) }.sortedBy { it.coords[z].first }
        val tops = Array(10) { _ -> Array(10) { _ -> zeroBrick } }
        bricks.forEach { putOnTop(it, tops) }
        return bricks
    }

    fun part1(input: List<String>): Int {
        val bricks = orderBricks(input)
        return bricks.count {it.canRemove }
    }

    fun part2(input: List<String>): Int {
        val important = mutableMapOf<String, Set<String>>()
        val bricks = orderBricks(input)
        return bricks
            .sumOf { b ->
                val map = b.depends
                    .map { important[it.toString()] ?: emptySet() }
                    .reduce { acc, a -> acc.intersect(a) }
                    .toMutableSet()
                if (b.depends.size == 1) {
                    val base = b.depends[0]
                    if (base != zeroBrick) map.add(base.toString())
                }
                important[b.toString()] = map
                map.size
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day22_test")
    part1(testInput).expect(5).println()

    val input = readInput("adv2023/Day22")
    part1(input).println("Part1")
    part2(testInput).expect(7).println()
    part2(input).println("Part2")
}
