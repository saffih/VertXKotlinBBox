package saffi.dataevent

import java.io.Serializable


data class DataEvent internal constructor(internal var event_type:

                                          String, internal var data: String, internal var timestamp: Long) : Serializable
