package saffi.verticles

import io.vertx.core.Future
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageConsumer
import saffi.helper.ArrayStreamLineReader

import java.io.IOException


class TestingReaderGenerator(private val eb: EventBus) : ReaderGenerator() {
    val fakeHelper = ArrayStreamLineReader()
    private val fakeConsumer: MessageConsumer<Any> = eb.consumer<Any>(JSONPump.fakePrefix) { msg -> fakeHelper.add(msg.body() as String) }

    override val line: String?
        @Throws(IOException::class)
        get() = fakeHelper.line

    override fun start(started: Future<Void>) {
        started.complete()
    }

    public override fun stop(stopped: Future<Void>) {
        fakeConsumer.unregister()
        stopped.complete()
    }
}
