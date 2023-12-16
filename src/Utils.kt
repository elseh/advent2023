import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun <T> T.println():T {
    println(this)
    return this
}

fun <T> T.println(prefix :String):T {
    kotlin.io.println("""$prefix: '$this'""")
    return this
}

fun <T> T.expect(expected: T):T {
    check(this == expected) {"found '$this' expected '$expected'"}
    return this
}

inline fun <T> splitBy(input: Iterable<T>, crossinline predicate: (T) -> Boolean) = sequence<List<T>> {
    var list = ArrayList<T>()
    for (item:T in input) {
        if (predicate(item)) {
            if (list.isNotEmpty()) {
                yield(list)
                list = ArrayList()
            }
        } else {
            list.add(item)
        }
    }
    if (list.isNotEmpty()) yield(list)
}
