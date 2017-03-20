package saffi.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.logging.LoggerFactory
import saffi.dataevent.DataEventCounter
import saffi.dataevent.DataEventHelper
import saffi.helper.VertXDeploymentOptionsFactory.getOptions
import saffi.verticles.JSONPumpAddress.broadcast


class EventSource : AbstractVerticle() {
    internal var logger = LoggerFactory.getLogger(EventSource::class.java)

    private val counter = DataEventCounter()
    private var brodcastConsumer: MessageConsumer<Any>? = null
    private var wordQueryConsumer: MessageConsumer<Any>? = null
    private var eventQueryConsumer: MessageConsumer<Any>? = null
    private var wordAllConsumer: MessageConsumer<Any>? = null
    private var eventAllConsumer: MessageConsumer<Any>? = null

    override fun start(started: Future<Void>) {
        val attachBus = Future.future<Void>()
        val spawnChild = Future.future<Void>()

        attachBus.setHandler { v -> startChildProcess(spawnChild) }

        spawnChild.setHandler(started.completer())

        setupMessageConsumers(attachBus)

    }

    private fun startChildProcess(spawnChild: Future<Void>) {
        val useFakeStream = config().getBoolean(disablePropertyName(), false)
        if (useFakeStream!!) {
            spawnChild.complete()
            return
        }
        vertx.deployVerticle("saffi.verticles.JSONPump", getOptions(this)
        ) { ar -> spawnChild.complete() }
    }

    private fun setupMessageConsumers(fut: Future<Void>) {
        val eb = vertx.eventBus()
        brodcastConsumer = eb.consumer<Any>(broadcast) { message ->
            val body = message.body() as String
            val de = DataEventHelper.fromJsonSilentFail(body)
            counter.add(de)
        }

        wordQueryConsumer = eb.consumer<Any>(EventSourceAddress.wordQuery) { message ->
            val word = message.body() as String
            val cnt = counter.wordCount.get(word)
            message.reply(cnt)
        }


        eventQueryConsumer = eb.consumer<Any>(EventSourceAddress.eventQuery) { message ->
            val word = message.body() as String
            val cnt = counter.eventCount.get(word)
            message.reply(cnt)
        }

        wordAllConsumer = eb.consumer<Any>(EventSourceAddress.wordAll
        ) { message -> message.reply(counter.wordCount.asJson()) }

        eventAllConsumer = eb.consumer<Any>(EventSourceAddress.eventAll
        ) { message -> message.reply(counter.eventCount.asJson()) }

        // Done setup consumers ready on event bus.
        fut.complete()
    }

    // Optional - called when verticle is undeployed
    override fun stop() {
        brodcastConsumer!!.unregister()
        wordQueryConsumer!!.unregister()
        eventQueryConsumer!!.unregister()
        wordAllConsumer!!.unregister()
        eventAllConsumer!!.unregister()
    }

    companion object {

        fun disablePropertyName(): String {
            return "disable." + EventSource::class.java.simpleName
        }
    }

}
