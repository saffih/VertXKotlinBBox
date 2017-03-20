package saffi.helper

import org.junit.Assert
import org.junit.Test

class WordCounterTest {

    @Test
    fun testEmpty() {
        val wordCounter = WordCounter()
        wordCounter.add(null)
    }


    @Test
    fun testGet() {
        val wordCounter = WordCounter()

        Assert.assertEquals(0, wordCounter.get("word"))
        wordCounter.add("word")
        Assert.assertEquals(1, wordCounter.get("word"))

        wordCounter.add("otherword")
        Assert.assertEquals(1, wordCounter.get("word"))

    }

    @Test
    fun testAsMap() {
        val wordCounter = WordCounter()

        wordCounter.add("word")
        Assert.assertEquals(1, wordCounter.asMap()["word"])

        for ((key, value) in wordCounter.asMap()) {
            Assert.assertEquals("word", key)
            Assert.assertEquals(1, value)
        }
    }

    @Test
    fun testAsJson() {
        val wordCounter = WordCounter()
        wordCounter.add("word")
        Assert.assertEquals("{\"word\":1}", wordCounter.asJson())
    }
}
