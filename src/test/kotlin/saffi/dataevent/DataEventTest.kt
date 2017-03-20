package saffi.dataevent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DataEventTest {
    @Test
    @Throws(Exception::class)
    fun equalsTest() {
        val a = DataEvent("a", "b", 0)
        val a2 = DataEvent("a", "b", 0)
        val ba = DataEvent("a", "a", 0)
        val bn = DataEvent("a", "b", 1)
        assertEquals(a, a2)
        assertNotEquals(a, ba)
        assertNotEquals(a, bn)
    }

    @Test
    @Throws(Exception::class)
    fun hashCodeTest() {
        val a = DataEvent("a", "b", 0)
        val a2 = DataEvent("a", "b", 0)
        val ba = DataEvent("a", "a", 0)
        val bn = DataEvent("a", "b", 1)
        assertEquals(a.hashCode().toLong(), a2.hashCode().toLong())
        assertNotEquals(a.hashCode().toLong(), ba.hashCode().toLong())
        assertNotEquals(a.hashCode().toLong(), bn.hashCode().toLong())

    }

}