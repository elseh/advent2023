package adv2023

import expect
import println
import readInput

fun main() {
    //%xz -> dj, zp
    data class Module(val name:String, val type:String, val output: List<String>,
                      var state:Boolean = false)
    fun parse(line:String) : Module {
        val (desc, outs) = line.split(" -> ")
        val output = outs.split(", ")
        return when (desc[0]) {
            '%' -> Module(desc.drop(1), "flip", output)
            '&' -> Module(desc.drop(1), "con", output)
            else -> Module(desc, "", output)
        }
    }

    fun emulate(schema: Map<String, Module>, inputs:Map<String, MutableSet<String>>,
                q: ArrayDeque<Triple<String, Boolean, String>>):Sequence<Triple<String, Boolean, String>> = sequence {
        yield(Triple("broadcaster", false, "button"))
        while (q.isNotEmpty()) {
            val (name, force, from) = q.removeFirst()
           // kotlin.io.println("$force, $name")

            if (!schema.containsKey(name)) continue
            val module = schema[name]!!
            val outForce =
                when (module.type) {
                    "flip" -> {
                        if (force) continue
                        else {
                           // kotlin.io.println("flip $name init ${module.state}")
                            module.state = !module.state
                            module.state
                        }
                    }

                    "con" -> {
                        if (force) inputs[name]!!.remove(from)
                        else inputs[name]!!.add(from)
                      //  kotlin.io.println("cons ${inputs[name]}")
                        inputs[name]!!.isNotEmpty()
                    }

                    else -> force
                }

            module.output.forEach {next ->
                q.addLast(Triple(next, outForce, name))
                yield(Triple(next, outForce, name))
            }
        }

    }

    fun part1(input: List<String>): Long {
        val schema = input.map { parse(it) }.associateBy { it.name }.println()
        val inputs = schema.flatMap { (_, mod) -> mod.output.map { it to mod.name } }
            .groupBy({ it.first }, { it.second})
            .map { (k, list) -> k to list.toMutableSet() }
            .associate{it.first to it.second}.println()
        val seed = listOf(Triple("broadcaster" ,false, "button"))
        (1..1000).flatMap {
            emulate(schema, inputs, ArrayDeque(seed))
        }
            .groupBy { it.second }.map { (k, v) -> k to v.count() }
            .let { return it[0].second.toLong() * it[1].second }

    }

    fun buildCalc(modules:Map<String, Module>):Long {
        return modules["broadcaster"]!!.output.map { start ->
            sequence {
                var current:String? = start
                var mod = modules[current]
                while (mod?.type == "flip") {
                    yield(mod.output.count { modules[it]!!.type == "con" })
                    current = mod.output.find { modules[it]!!.type == "flip" }
                    mod = current.let { modules[it] }
                }
            }.toList().reduceRight { v, a -> a * 2 + v }.toLong().println("start $start")
        }.reduce { a, v -> a * v }
    }

    fun part2(input: List<String>): Long {
        val schema = input.map { parse(it) }.associateBy { it.name }
        return buildCalc(schema)
//
    }

    // test if implementation meets criteria from the description, like:
    part1(readInput("adv2023/Day20_test1")).expect(32000000).println()
    part1(readInput("adv2023/Day20_test2")).expect(11687500).println()

    val input = readInput("adv2023/Day20")
    part1(input).println("Part1")
    // lazy solver for the final input
    part2(input).println("Part2")

}
