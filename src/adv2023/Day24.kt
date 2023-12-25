package adv2023

import adv2023.Alg.gcd
import adv2023.Alg.isZero
import expect
import println
import readInput
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.round

object Alg {
    fun isZero(d: Double) = abs(d) < 1e-5

    fun gcd(a:Long, b:Long): Long {
        if (a < 0) return gcd(b, -a)
        if (b == 0L) return a
        return gcd(b, a % b)
    }

    fun gcd(a:BigInteger, b:BigInteger): BigInteger {
        if (a < BigInteger.ZERO) return gcd(b, -a)
        if (b == BigInteger.ZERO) return a
        return gcd(b, a % b)
    }
}
fun main() {

    fun crossProduct(v1: List<Long>, v2: List<Long>): List<Long> {
        return listOf(
            v1[1] * v2[2] - v1[2] * v2[1],
            v1[2] * v2[0] - v1[0] * v2[2],
            v1[0] * v2[1] - v1[1] * v2[0]
        )
    }

    fun dotProduct(v1: List<BigInteger>, v2: List<BigInteger>): BigInteger {
        return v1.zip(v2).sumOf { it.first * it.second }
    }
    data class Hail(val pos:List<Long>, val vel:List<Long>) {
        fun eq1() :List<Double> {
            val (x, y) = pos.take(2)
            val (dx, dy) = vel.take(2)
            val rel = dx.toDouble() / dy
            return listOf(1.0, -rel, y * rel - x)
        }
        fun time(point: List<Double>) : Double {
            // x + dxT = X, T = X - x / dx
            return (point[0] - pos[0])/vel[0]
        }

    }

    fun intersect1(h1:Hail, h2:Hail):List<Double>? {
        val (a1, d1) = h1.eq1().takeLast(2)
        val (a2, d2) = h2.eq1().takeLast(2)
        if (isZero(a1 - a2)) return null // parallel
        val y = (d2 - d1)/(a1 - a2)
        val x = (-d1 - y * a1)
        return listOf(x, y)
    }


    fun parseInput(input: List<String>) = input.map { line ->
        line.split(", ", " @ ")
            .map { it.trim() }
            .map { it.toLong() }.chunked(3)
    }
        .map { (a, b) -> Hail(a, b) }

    fun part1(input: List<String>, range:List<Long>): Int {
        val hails = parseInput(input)
        return hails.sumOf { a ->
            hails.asSequence().filter { it != a }
                .mapNotNull { b ->
                    val point = intersect1(a, b)
                    if (point != null && b.time(point) >= 0) point else null
                }
                .filter { it[0] >= range[0] && it[0] <= range[1] }
                .filter { it[1] >= range[0] && it[1] <= range[1] }
                .count { a.time(it) >= 0 }
        } /2
    }


    fun combine(
        pos: List<Long>,
        pos1: List<Long>,
        i: Long
    ) = (pos zip pos1).map { it.first + i * it.second }

    fun solve4(hails:List<Hail>): Hail {
        val first = hails.first()
        val fixed = hails.map { h -> Hail(combine(h.pos, first.pos, -1), combine(h.vel, first.vel, -1)) }
        // F0 becomes a dot.
        // F0 with F1 form a plane defined by its normal vector
        val norm = crossProduct(fixed[1].vel, fixed[1].pos)
            .let { product ->
                val lcd = product.reduce { a, v -> gcd(a, v) }
                product.map { it.toBigInteger() / lcd.toBigInteger() }
            }
        // time to compute 2 intersections:
        // F2, F3 with a plane t = pN/vN
        val times = (2..3)
            .map { fixed[it] }
            .map {h ->
                val products = listOf(h.pos, h.vel)
                    .map { v -> dotProduct(v.map { it.toBigInteger() }, norm) }
                val gcd = gcd(products[0], products[1])
                products.map { it/gcd }
                    .map { it.abs() }
            }
        val points = (2..3).map { hails[it] }.zip(times)
            .map { (h, t) -> (h.pos zip h.vel).map { it.first.toBigInteger() + t.first() * it.second.toBigInteger() / t.last() } }
        val vel = points[0].zip(points[1]).map { (a, b) -> (a - b)*times[0].last() * times[1].last() / (times[0].first() * times[1].last() - times[1].first()* times[0].last()) }
        val pos = points[0].zip(vel).map { it.first - times[0].first() * it.second / times[0].last()}
        return Hail(pos.map { it.toLong() }, vel.map { it.toLong() })
    }

    fun part2(input: List<String>): Long {
        val hails = parseInput(input)
        val solutions = hails.windowed(4).map { solve4(it) }.distinct().println("different solutions")
        solutions
            .map { s ->
                s.println()
                hails.map { h ->
                    val point = intersect1(h, s)
                    val time = round(h.time(point!!)).toLong()
                    (combine(h.pos, h.vel, time) == combine(s.pos, s.vel, time))
                }
                    .count { it }.println("intersects with").expect(input.size)
            }
        return solutions.first().pos.sum()

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("adv2023/Day24_test")
    val input = readInput("adv2023/Day24")
    part1(testInput, listOf(7, 27)).expect(2).println()
    part1(input, listOf(200000000000000, 400000000000000)).println("Part1")
    part2(testInput).expect(47).println()
    part2(input).println("Part2")
}