package saffi.dataevent

import saffi.helper.NonBlockingStreamLineReader

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class DataStreamHelper(stream: InputStream) : NonBlockingStreamLineReader(stream) {

    override val line: String?
        @Throws(IOException::class)
        get() {
            while (true) {
                var st: String? = super.line ?: return null
                st = DataEventHelper.fromJsonAndBackSilentFail(st)
                if (st != null) {
                    return st
                }
            }
        }

    companion object {

        fun getDataStreamHelper(st: String): DataStreamHelper {
            return DataStreamHelper(getByteArrayInputStream(st))
        }

        fun getByteArrayInputStream(example: String): ByteArrayInputStream {
            return ByteArrayInputStream(example.toByteArray(StandardCharsets.UTF_8))
        }
    }
}
