package saffi.dataevent

import org.junit.Assert
import org.junit.Test

class DataEventWordCounterTest {
    @Test
    fun testAdd() {
        val de = DataEvent("type", "data", 1L)

        val counter = DataEventCounter()
        Assert.assertEquals(0, counter.eventCount.get("type").toInt().toLong())
        Assert.assertEquals(0, counter.wordCount.get("data").toInt().toLong())

        counter.add(de)
        Assert.assertEquals(1, counter.eventCount.get("type").toInt().toLong())
        Assert.assertEquals(1, counter.wordCount.get("data").toInt().toLong())
    }
}
