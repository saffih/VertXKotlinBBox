package saffi.dataevent

import com.google.gson.Gson

object DataEventHelper {
    private val g = Gson()

    private fun fromJson(st: String): DataEvent {
        return g.fromJson<DataEvent>(st, DataEvent::class.java)
    }

    fun toJson(de: DataEvent): String {
        return g.toJson(de)
    }

    fun fromJsonSilentFail(st: String): DataEvent? {
        try {
            return fromJson(st)
        } catch (e: RuntimeException) {
            // logger
            return null
        }

    }

    internal fun fromJsonAndBackSilentFail(st: String?): String? {
        if (st == null) {
            return null
        }
        try {
            return toJson(fromJson(st))
        } catch (e: RuntimeException) {
            // logger
            return null
        }

    }
}
