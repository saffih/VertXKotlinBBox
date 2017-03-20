package saffi.verticles


object EventSourceAddress {
    val eventAll: String
        get() = addressPrefix + ".eventcount.all"

    val wordAll: String
        get() = addressPrefix + ".wordcount.all"

    val eventQuery: String
        get() = addressPrefix + ".eventcount.query"

    val wordQuery: String
        get() = addressPrefix + ".wordcount.query"

    private val addressPrefix: String
        get() = "saffi.dataevent"
}
