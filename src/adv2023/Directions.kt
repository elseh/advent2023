package adv2023

/*
         3
        1, 0,
         2
 */
object Directions {

    val dir = mapOf(
        'R' to 0,
        'L' to 1,
        'U' to 3,
        'D' to 2
    )

    val LEFT = dir['L']!!
    val RIGHT = dir['R']!!
    val UP = dir['U']!!
    val DOWN = dir['D']!!

    fun applyStep(pos: Pair<Int, Int>, step: Pair<Int, Int>) :Pair<Int, Int> {
        val (d, len) = step
        val (x, y) = pos
        val change = ((d and 1) * 2 - 1) * len
        return if (d >= 2) x to y - change else x - change to y
    }

    fun clock(d: Int):Int {
        //00 -> 10 -> 01 -> 11 -> 00
        val a = d shr 1;
        val b = d and 1
        return ((a xor 1) shl 1) or (a xor b)
    }
}