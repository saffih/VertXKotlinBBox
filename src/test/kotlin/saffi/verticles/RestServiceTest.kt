package saffi.verticles


import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import saffi.helper.VertXDeploymentOptionsFactory
import java.io.IOException

@RunWith(VertxUnitRunner::class)
class RestServiceTest {
    val vertx = Vertx.vertx()
    val eb = vertx.eventBus()

    val options = VertXDeploymentOptionsFactory.testOptions
    val port = options.config.getInteger("http.port", RestService.PORT_DEFAULT)
    val testhost
        get() = options.config.getString("test.host", "localhost")

    @Before
    @Throws(IOException::class)
    fun setUp(context: TestContext) {
        vertx.deployVerticle("saffi.verticles.RestService", options, context.asyncAssertSuccess<String>())
    }

    @After
    @Throws(InterruptedException::class)
    fun after(context: TestContext) {
        vertx.close(context.asyncAssertSuccess<Void>())
        Thread.sleep(500)
    }


    @Test(timeout = 10000)
    fun testQueryWord(context: TestContext) {

        val async1 = context.async()
        val client = vertx.createHttpClient()
        val id = "ping"
        val cnt = 4

        eb.consumer<Any>(EventSourceAddress.wordQuery) { message ->
            context.assertEquals(id, message.body())
            message.reply(cnt)
        }

        val req = client.get(port, testhost, "/word/" + id)

        req.exceptionHandler { err -> context.fail(err.message) }
        req.handler { resp ->
            context.assertEquals(200, resp.statusCode())
            resp.bodyHandler { body ->
                val resJson = String(body.bytes)
                val c = HashMap::class.java
                val res = Json.decodeValue(resJson, c)
                context.assertEquals(cnt, res.get(id))
                async1.complete()

            }
        }
        req.end()
    }

    @Test(timeout = 1000)
    fun testQueryEvent(context: TestContext) {

        val async1 = context.async()
        val client = vertx.createHttpClient()
        val id = "pong"
        val cnt = 5
        val req = client.get(port, testhost, "/event/" + id)

        val eb = vertx.eventBus()
        eb.consumer<Any>(EventSourceAddress.eventQuery) { message ->
            context.assertEquals(id, message.body())
            message.reply(cnt)
        }

        req.exceptionHandler { err -> context.fail(err.message) }
        req.handler { resp ->
            context.assertEquals(200, resp.statusCode())
            async1.complete()
        }
        req.end()
    }

    @Test(timeout = 1000)
    fun testAllWords(context: TestContext) {

        val async1 = context.async()
        val client = vertx.createHttpClient()
        val data = "data"
        val req = client.get(port, testhost, "/words")

        val eb = vertx.eventBus()
        eb.consumer<Any>(EventSourceAddress.wordAll) { message -> message.reply(data) }

        req.exceptionHandler { err -> context.fail(err.message) }
        req.handler { resp ->
            context.assertEquals(200, resp.statusCode())
            async1.complete()
        }
        req.end()
    }

    @Test(timeout = 1000)
    fun testAllEvents(context: TestContext) {

        val async1 = context.async()
        val client = vertx.createHttpClient()
        val data = "data"
        val req = client.get(port, testhost, "/events")

        val eb = vertx.eventBus()
        eb.consumer<Any>(EventSourceAddress.eventAll) { message -> message.reply(data) }

        req.exceptionHandler { err -> context.fail(err.message) }
        req.handler { resp ->
            context.assertEquals(200, resp.statusCode())
            async1.complete()
        }
        req.end()
    }

}
