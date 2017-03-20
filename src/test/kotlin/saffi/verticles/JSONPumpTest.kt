package saffi.verticles


import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import saffi.helper.VertXDeploymentOptionsFactory
import java.io.IOException

@RunWith(VertxUnitRunner::class)
class JSONPumpTest {
    val vertx = Vertx.vertx()
    val options = VertXDeploymentOptionsFactory.testOptions

    @Before
    @Throws(IOException::class)
    fun setUp(context: TestContext) {
        options.config.put(JSONPump.fakePrefix, true)
        vertx.deployVerticle("saffi.verticles.JSONPump",
                options, context.asyncAssertSuccess<String>())
    }

    @After
    fun after(context: TestContext) {

        vertx.close(context.asyncAssertSuccess<Void>())
    }


    @Test(timeout = 3000)
    fun testWithFakeGoodEvent(context: TestContext) {
        val async = context.async()

        val st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n"
        val message = arrayOfNulls<String>(1)
        val eb = vertx.eventBus()
        val handle_msg = { msg: Message<Any> ->
            message[0] = msg.body() as String
            context.assertEquals(st, message[0])
            async.complete()
        }
        val c = eb.consumer(JSONPumpAddress.broadcast, handle_msg)
        eb.send(JSONPump.fakePrefix, st)
    }

}
