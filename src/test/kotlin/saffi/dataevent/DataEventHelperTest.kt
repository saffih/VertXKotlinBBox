package saffi.dataevent

import org.junit.Assert
import org.junit.Test
import java.util.*

class DataEventHelperTest {

    @Test
    fun testDataEventParse() {
        val de = DataEvent("type", "data", 1334L)
        val st = DataEventHelper.toJson(de)
        val de2 = DataEventHelper.fromJsonSilentFail(st)
        Assert.assertEquals(de, de2)
        val map = HashMap<DataEvent, DataEvent>()
        map.put(de, de)
        Assert.assertTrue(map[de2] === de)
    }

    @Test
    fun tesFailSafe() {
        val de2 = DataEventHelper.fromJsonSilentFail("{{1,2,3}")
        Assert.assertNull(de2)
        val st = DataEventHelper.fromJsonAndBackSilentFail("{{1,2,3}")
        Assert.assertNull(st)
    }
}
