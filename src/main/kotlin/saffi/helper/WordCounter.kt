package saffi.helper

import com.google.gson.Gson
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * For single threaded use.
 * Iterator of the collection.
 */
class WordCounter : Cloneable {

    // rate is slow enough - we do not need long.
    private val counter = ConcurrentHashMap<String, Int>()

    fun add(st: String?) {
        if (st == null) {
            return
        }
        val map = counter //as java.util.Map<String, Int>
        counter.put(st, map.getOrDefault(st, 0) + 1)
    }

    operator fun get(st: String): Int {
        val map = counter //as java.util.Map<String, Int>
        return map.getOrDefault(st, 0)
    }


    fun asMap(): Map<String, Int> {
        return Collections.unmodifiableMap(counter)
    }


    fun asJson(): String {
        val g = Gson()
        return g.toJson(counter)
    }

}
