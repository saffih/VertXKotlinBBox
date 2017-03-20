package saffi.helper

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by saffi on 15/03/17.
 */
class ArrayStreamLineReaderTest {
    @Before
    @Throws(Exception::class)
    fun setUp() {

    }

    @After
    @Throws(Exception::class)
    fun tearDown() {

    }

    @Test
    @Throws(Exception::class)
    fun empty() {
        val a = ArrayStreamLineReader()
        assert(a.line == null)
    }

    @Test
    @Throws(Exception::class)
    fun getBack() {
        val a = ArrayStreamLineReader()
        val line = "line"
        a.add(line)
        assert(a.line == line)
        assert(a.line == null)
    }

}