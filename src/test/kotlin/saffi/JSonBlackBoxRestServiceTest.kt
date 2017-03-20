package saffi


import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import saffi.helper.VertXDeploymentOptionsFactory
import saffi.verticles.RestService
import java.io.IOException
import java.util.*

@RunWith(VertxUnitRunner::class)
class JSonBlackBoxRestServiceTest {

    internal val vertx = Vertx.vertx()
    val options = VertXDeploymentOptionsFactory.testOptions
    val port = options.config.getInteger("http.port", RestService.PORT_DEFAULT)
    val testhost = options.config.getString("test.host", "localhost")

    @Before
    @Throws(IOException::class)
    fun setUp(context: TestContext) {
        vertx.deployVerticle("saffi.JSonBlackBoxRestService", options,
                context.asyncAssertSuccess<String>())
    }

    @After
    fun after(context: TestContext) {
        vertx.close(context.asyncAssertSuccess<Void>())
    }


    @Test(timeout = 2000)
    fun testEvents(context: TestContext) {

        val async1 = context.async()
        val client = vertx.createHttpClient()
        val req = client.get(port, testhost, "/events")

        req.exceptionHandler { err -> context.fail(err.message) }
        req.handler { resp ->
            context.assertEquals(200, resp.statusCode())

            resp.bodyHandler { body ->
                val resJson = String(body.bytes)
                context.assertEquals("{}", resJson)
                async1.complete()
            }
        }
        req.end()
    }


    @Test(timeout = 2000)
    fun testWords(context: TestContext) {

        val async1 = context.async()
        val client = vertx.createHttpClient()
        val req = client.get(port, testhost, "/words")

        req.exceptionHandler { err -> context.fail(err.message) }
        req.handler { resp ->
            context.assertEquals(200, resp.statusCode())

            resp.bodyHandler { body ->
                val resJson = String(body.bytes)
                // real app the first event is fast
                //				context.assertEquals("{}", resJson);
                async1.complete()
            }
        }
        req.end()
    }


    @Test(timeout = 3000)
    fun blackBoxTypeBaz(context: TestContext) {
        val async = context.async()

        val foundBaz = Future.future<String>()
        pollForEvent("baz", context, foundBaz)

        val foundBazDone = Future.future<String>()
        foundBaz.setHandler { res ->
            println("found baz " + res.result())
            foundBazDone.complete()
            async.complete()
        }
    }


    @Test(timeout = 15000)
    fun blackBoxTypeBazAndBar(context: TestContext) {
        val async = context.async()

        val foundBaz = Future.future<String>()
        val foundBar = Future.future<String>()

        pollForEvent("baz", context, foundBaz)
        pollForEvent("bar", context, foundBar)

        val foundBazDone = Future.future<String>()
        val foundBarDone = Future.future<String>()

        foundBar.setHandler { res ->
            println("found bar" + res.result())
            foundBarDone.complete()
        }

        foundBaz.setHandler { res ->
            println("found baz" + res.result())
            foundBazDone.complete()
        }

        CompositeFuture.join(foundBarDone, foundBazDone).setHandler { res -> async.complete() }

    }

    fun pollForEvent(id: String, context: TestContext, success: Future<String>) {
        val found = Future.future<String>()

        val timerid = longArrayOf(0)

        timerid[0] = vertx.setPeriodic(100) { queryFor(context, found, id) }

        found.setHandler { ar ->
            vertx.cancelTimer(timerid[0])
            success.complete(found.result())
        }
    }

    fun queryFor(context: TestContext, success: Future<String>, id: String) {
        val client = vertx.createHttpClient()
        val req = client.get(port, testhost, "/event/" + id)

        req.exceptionHandler { err -> context.fail(err.message) }
        req.handler { resp ->
            context.assertEquals(200, resp.statusCode())

            resp.bodyHandler { body ->
                val resJson = String(body.bytes)
                val c = HashMap::class.java
                val res = Json.decodeValue(resJson, c)
                val value = res.get(id)

                if (0 == value) {
                    // waiting - next one might be a match.
                    print(".")
                } else {
                    success.complete(resJson)
                }
            }
        }
        req.end()
    }
}
