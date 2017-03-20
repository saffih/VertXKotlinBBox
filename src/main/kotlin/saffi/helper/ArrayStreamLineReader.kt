package saffi.helper

import java.io.IOException
import java.util.*

/**
 * Created by saffi on 26/09/16.
 */
class ArrayStreamLineReader : StreamLineReader {
    private val buffer = ArrayList<String>()

    fun add(line: String): Boolean {
        return buffer.add(line)
    }

    fun addAll(lines: Collection<String>): Boolean {
        return buffer.addAll(lines)
    }

    override val line: String?
        @Throws(IOException::class)
        get() {
            if (buffer.isEmpty()) {
                return null
            }
            return buffer.removeAt(0)
        }
}
