package saffi.helper

import org.junit.Assert
import org.junit.Test
import saffi.dataevent.DataStreamHelper.Companion.getByteArrayInputStream
import java.io.IOException

class NonBlockingStreamLineReaderTest {

    @Test
    @Throws(IOException::class)
    fun testEmpty() {
        val stream = getByteArrayInputStream("")
        val sth = NonBlockingStreamLineReader(stream)
        sth.line
    }

    @Test
    @Throws(IOException::class)
    fun testGoodMessage() {
        val example = "{{{{{ \"event_type\": \"baz\", \"data\": \"ipsum\", \"timestamp\": 1474449973 }\n" +
                "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n" +
                "{ \"%�\\~��\n" +
                "{ \"event_type\": \"bar\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n" +
                "H�+��\n" +
                "{ \"event_type\": \"bar\", \"data\": \"dolor\", \"timestamp\": 1474449980 }\n" +
                "{ \"event_type\": \"foo\", \"data\": \"ipsum\", \"timestamp\": 1474449980 }\n" +
                "{ \"s    ��\n" +
                "{ \"event_type\": \"bar\", \"data\": \"ipsum\", \"timestamp\": 1474449991 }\n" +
                "{ \"event_type\": \"baz\", \"data\": \"ipsum\", \"timestamp\": 1474449994 }\n" +
                "last\n" + " ignores"
        val stream = getByteArrayInputStream(example)
        val sth = NonBlockingStreamLineReader(stream)
        val (cnt, last: String) = count(sth)
        Assert.assertEquals(11, cnt.toLong())
        Assert.assertEquals("last\n", last)

    }

    private fun count(sth: NonBlockingStreamLineReader): Pair<Int, String> {
        var cnt = 0
        var st: String?
        var last: String = ""
        while (true) {
            st = sth.line
            if (st == null) break
            last = st
            cnt++
        }
        return Pair(cnt, last)
    }

}
