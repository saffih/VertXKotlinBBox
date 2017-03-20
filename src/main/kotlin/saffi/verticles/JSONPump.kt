package saffi.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import saffi.helper.StreamLineReader
import java.io.IOException


class JSONPump : AbstractVerticle() {
//    internal var logger = LoggerFactory.getLogger(JSONPump::class.java)

    private var timerId: Long = 0

    // use getter since config context is not ready till it is used.
    private val pollInterval: Int
        get() = config().getInteger("pollInterval", 100)

    private val command: String
        get () = config().getString("blackbox", "./generator-linux-amd64")


    // since we read in a non blocking way - we do not need to put it within a worker
    // vertx.executeBlocking(future -> {...}, res -> {});

    val realGenerator
        get () = BlackBoxReaderGenerator(command)

    val testGenerator
        get() = TestingReaderGenerator(vertx.eventBus())

    // for testing - choose the appropriate generator depending on config settings.
    fun readerGeneratorGetter(): ReaderGenerator {
        val testing = config().getBoolean(fakePrefix, false)
        return if (testing) testGenerator else realGenerator
    }

    val readerGenerator
        get() = readerGeneratorGetter()

    override fun start(started: Future<Void>) {

        pollStreamSendEvents(readerGenerator)
        readerGenerator.start(started)
    }

    // Optional - called when verticle is undeployed
    override fun stop(stopped: Future<Void>) {
        vertx.cancelTimer(timerId)
        readerGenerator.stop(stopped)
    }

    private fun pollStreamSendEvents(streamLineReader: StreamLineReader) {
        timerId = vertx.setPeriodic(pollInterval.toLong()) { id ->
            while (consumeAvailable(streamLineReader) != null) {
                // do that again
            }
        }
    }


    private fun consumeAvailable(helper: StreamLineReader): String? {
        val eb = vertx.eventBus()

        try {
            val st = helper.line ?: return null
            eb.publish(JSONPumpAddress.broadcast, st)
            return st
        } catch (e: IOException) {
            // failures should crash and a new one should be created
            throw RuntimeException("failed readiing stream ", e)
        }

    }

    companion object {
        val fakePrefix = "fake." + JSONPump::class.java.simpleName
    }
}

