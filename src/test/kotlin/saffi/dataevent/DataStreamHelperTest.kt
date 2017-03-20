package saffi.dataevent

import org.junit.Assert
import org.junit.Test

import java.io.IOException


class DataStreamHelperTest {

    @Test
    @Throws(IOException::class)
    fun testHelper() {
        val hello = "Hello"
        val resNull = DataStreamHelper.getDataStreamHelper(hello).line
        Assert.assertNull(resNull)
        val st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n"

        val res = DataStreamHelper.getDataStreamHelper(st).line
        Assert.assertNotNull(res)
    }
}
