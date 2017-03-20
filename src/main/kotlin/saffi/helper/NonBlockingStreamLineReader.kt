package saffi.helper

import java.io.*

open class NonBlockingStreamLineReader(stream: InputStream) : StreamLineReader {
    private val stream: InputStream
    private var br: BufferedReader? = null
    private var sb = StringBuilder()

    init {

        try {
            this.stream = stream
            this.br = BufferedReader(InputStreamReader(stream, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }

    }

    override // todo use char []
    val line: String?
        @Throws(IOException::class)
        get() {

            var found: String? = null
            while (br!!.ready()) {
                val c = br!!.read().toChar()
                sb.append(c)
                if (c == '\n') {
                    found = sb.toString()
                    sb = StringBuilder()
                    return found
                }
            }
            return null
        }

}
