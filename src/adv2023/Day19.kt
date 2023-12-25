package adv2023

import expect
import println
import readInput
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

fun main() {
    data class Gear(val params:Map<Char, Int>, var state:String = "in")
    data class Rule(val result:String, val p: Char, val op:Char, val limit:Int) {
        fun applicable(gear:Gear): Boolean {
            val param = gear.params[p]!!
            return when (op) {
                '>' -> param > limit
                '<' -> param < limit
                else -> throw IllegalArgumentException("unknown op $op")
            }
        }
    }
    data class Procedure(val name:String, val rules:List<Rule>)

    fun parseGear(line:String):Gear {
        return line.drop(1).dropLast(1).split(",")
            .associate { chunk -> chunk[0] to chunk.drop(2).toInt() }
            .let { Gear(it) }
    }

    parseGear("{x=787,m=2655,a=1222,s=2876}")
        .expect(Gear(mapOf('x' to 787, 'm' to 2655, 'a' to 1222, 's' to 2876), state = "in"))

    val max = 4000
    fun parseOp(line:String):Procedure {
        var list = line.dropLast(1).split("{", ",", ":")
        val name = list.first()
        val last = list.last()
        list = list.drop(1).dropLast(1)
        return list
            .chunked(2).map { (l, name) -> Rule(name, l[0], l[1], l.drop(2).toInt())}
            .let { Procedure(name, it + listOf(Rule(last, 'a', '>', 0))) }
    }

    parseOp("hdj{m>838:A,pv}").also {
        it.name.expect("hdj")
        it.rules.last().result.expect("pv")
    }

    fun process(gear: Gear, ops: Map<String, Procedure>):Gear {
        while (ops.containsKey(gear.state)) {
            val op = ops[gear.state]!!
            op.rules.find { it.applicable(gear) }.let { gear.state = it!!.result }
        }
        return gear
    }

    fun part1(input: List<String>): Int {
        val procedures = input.takeWhile { it.isNotEmpty() }
            .map(::parseOp).associateBy { it.name }
        return input.asSequence().dropWhile { it.isNotEmpty() }.drop(1)
            .map(::parseGear)
            .map { process(it, procedures) }
            .filter { it.state == "A" }
            .sumOf { it.params.values.sum() }

    }

    data class State(val ranges: Map<Char, Pair<Int, Int>>) {
        fun isEmpty():Boolean {
            return ranges.any { (_, r) -> r.first >= r.second }
        }
        fun cut(rule: Rule):List<State> {
            val mutableRanges = ranges.toMutableMap()
            val p = rule.p
            val orig = ranges[p]!!
            return when (rule.op) {
                '>' -> listOf(
                    State(mutableRanges.also { it[p] = max(rule.limit + 1, orig.first) to orig.second }.toMap()),
                    State(mutableRanges.also { it[p] = orig.first to min(rule.limit, orig.second) }.toMap())
                )
                '<' -> listOf(
                    State(mutableRanges.also { it[p] = orig.first to min(rule.limit - 1, orig.second) }.toMap()),
                    State(mutableRanges.also { it[p] = max(rule.limit, orig.first) to orig.second }.toMap())
                )
                else -> throw IllegalStateException("unknown op")
            }
        }
    }

    fun check(proc:Map<String, Procedure>, current: String, state:State):Sequence<State> = sequence {
        if (current == "R" || state.isEmpty()) return@sequence
        if (current == "A") {
            yield(state)
            return@sequence
        }
        val op = proc[current]!!
        var rest = state
        for (rule in op.rules) {
            val (f, s) = rest.cut(rule)
            yieldAll(check(proc, rule.result, f))
            rest = s
        }
    }

    fun part2(input: List<String>): Long {
        val procedures = input.takeWhile { it.isNotEmpty() }
            .map(::parseOp).associateBy { it.name }
        val state = State(mapOf(
            'x' to (1 to max),
            'm' to (1 to max),
            'a' to (1 to max),
            's' to (1 to max),
        ))
        return check(procedures, "in", state)
            .sumOf { st -> st.ranges.map { it.value.second - it.value.first+1 }
                .fold(1L){a, b -> a * b}
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day19_test")
    part1(testInput).expect(19114).println()

    val input = readInput("adv2023/Day19")
    part1(input).println("Part1")

    part2(testInput).expect(167409079868000).println()
    part2(input).println("Part2")
}
