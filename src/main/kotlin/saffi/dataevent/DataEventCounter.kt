package saffi.dataevent

import saffi.helper.WordCounter


class DataEventCounter {

    // rate is slow enough - we do not need long.
    val wordCount = WordCounter()
    val eventCount = WordCounter()

    fun add(de: DataEvent?) {
        if (de == null) {
            return
        }
        eventCount.add(de.event_type)
        wordCount.add(de.data)
    }
}
