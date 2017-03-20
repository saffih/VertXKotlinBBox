package saffi.verticles


import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import saffi.dataevent.DataEventHelper
import saffi.helper.VertXDeploymentOptionsFactory

@RunWith(VertxUnitRunner::class)
class EventSourceTest {
    val vertx = Vertx.vertx()
    val options = VertXDeploymentOptionsFactory.testOptions

    @Before
    fun setUp(context: TestContext) {
        options.config.put(EventSource.disablePropertyName(), true)
        vertx.deployVerticle("saffi.verticles.EventSource", options, context.asyncAssertSuccess<String>())
    }

    @After
    fun after(context: TestContext) {
        vertx.close(context.asyncAssertSuccess<Void>())
    }


    @Test(timeout = 1000)
    fun testIgnoreBadEventsEvent(context: TestContext) {

        val async = context.async()
        val st = " \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }"
        val eb = vertx.eventBus()

        eb.send(JSONPumpAddress.broadcast, st)
        eb.send<Any>(EventSourceAddress.wordQuery, "baz"
        ) { reply ->
            context.assertEquals(0, reply.result().body())
            async.complete()
        }
    }

    @Test(timeout = 3000L)
    fun testGoodEventsEvent(context: TestContext) {
        val async = context.async()
        val st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }"
        val eb = vertx.eventBus()

        val fut1 = Future.future<Void>()
        val fut2 = Future.future<Void>()

        fut1.setHandler { v ->
            eb.send<Any>(EventSourceAddress.eventQuery, "baz"
            ) { reply ->
                context.assertEquals(0, reply.result().body())
                eb.publish(JSONPumpAddress.broadcast, st)
                fut2.complete()
            }
        }

        fut2.setHandler { v ->
            eb.send<Any>(EventSourceAddress.eventQuery, "baz"
            ) { reply ->
                context.assertEquals(1, reply.result().body())
                async.complete()
            }
        }

        fut1.complete()
    }

    @Test(timeout = 1000)
    fun testIgnoreBadWordEvent(context: TestContext) {
        val async = context.async()
        val st = " \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }"
        val eb = vertx.eventBus()

        eb.send(JSONPumpAddress.broadcast, st)
        eb.send<Any>(EventSourceAddress.wordQuery, "baz") {
            reply ->
            context.assertEquals(0, reply.result().body())
            async.complete()
        }
    }

    @Test(timeout = 3000L)
    fun testGoodWordEvent(context: TestContext) {
        val async = context.async()
        val st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }"
        val eb = vertx.eventBus()

        val fut1 = Future.future<Void>()
        val fut2 = Future.future<Void>()

        val s = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }"
        val de = DataEventHelper.fromJsonSilentFail(s)

        fut1.setHandler {
            eb.send<Any>(EventSourceAddress.eventQuery, "baz") {
                reply ->
                context.assertEquals(0, reply.result().body())
                eb.publish(JSONPumpAddress.broadcast, st)
                fut2.complete()
            }
        }

        fut2.setHandler {
            eb.send<Any>(EventSourceAddress.eventQuery, "baz") {
                reply ->
                context.assertEquals(1, reply.result().body())
                async.complete()
            }
        }

        fut1.complete()
    }


    @Test(timeout = 1000L)
    fun testAllEvents(context: TestContext) {
        val async = context.async()
        val st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }"
        val eb = vertx.eventBus()

        val fut1 = Future.future<Void>()
        val fut2 = Future.future<Void>()

        fut1.setHandler {
            eb.send<Any>(EventSourceAddress.eventAll, "") {
                reply ->
                context.assertEquals("{}", reply.result().body())
                eb.publish(JSONPumpAddress.broadcast, st)
                fut2.complete()
            }
        }

        fut2.setHandler {
            eb.send<Any>(EventSourceAddress.eventAll, "") {
                reply ->
                context.assertEquals("{\"baz\":1}", reply.result().body())
                async.complete()
            }
        }

        fut1.complete()
    }


    @Test(timeout = 1000L)
    fun testAllWords(context: TestContext) {
        val async = context.async()
        val st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }"
        val eb = vertx.eventBus()

        val fut1 = Future.future<Void>()
        val fut2 = Future.future<Void>()

        fut1.setHandler {
            eb.send<Any>(EventSourceAddress.wordAll, "") {
                reply ->
                context.assertEquals("{}", reply.result().body())
                eb.publish(JSONPumpAddress.broadcast, st)
                fut2.complete()
            }
        }

        fut2.setHandler {
            eb.send<Any>(EventSourceAddress.wordAll, "") {
                reply ->
                context.assertEquals("{\"dolor\":1}", reply.result().body())
                async.complete()
            }
        }

        fut1.complete()
    }
}
