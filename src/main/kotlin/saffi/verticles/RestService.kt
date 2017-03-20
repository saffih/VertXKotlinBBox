package saffi.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext


class RestService : AbstractVerticle() {
    companion object {
        val PORT_DEFAULT = 8080
    }

    internal var logger = LoggerFactory.getLogger(RestService::class.java)


    fun createServer(serverReady: Future<HttpServer>): HttpServer {
        val port = context.config().getInteger("http.port", PORT_DEFAULT)
        val router = buildRoutes()

        val boundServer = vertx.createHttpServer().requestHandler({
            req ->
            router.accept(req)
        }
        ).listen(port, serverReady.completer())
        return boundServer
    }

    private var server: HttpServer? = null

    override fun start(started: Future<Void>) {
        val serverReady = Future.future<HttpServer>().setHandler({
            hs ->
            started.complete()
        }
        )

        server = createServer(serverReady)
    }


    override fun stop(stopped: Future<Void>) {
        server!!.close(stopped.completer())
    }

    private fun buildRoutes(): Router {
        val router = Router.router(vertx)

        router.get("/words").handler(allWordHandler())
        router.get("/events").handler(allTypeHandler())
        router.get("/word/:id").handler(queryWordHandler())
        router.get("/event/:id").handler(queryEventHandler())
        return router
    }

    private fun allTypeHandler(): Handler<RoutingContext> {
        val eb: EventBus = vertx.eventBus()
        return Handler({ rc ->
            eb.send<Any>(EventSourceAddress.eventAll, "") { reply ->
                val msg = reply.result()
                val st = msg.body() as String
                rc.response().putHeader("content-type", "application/json").end(st)
            }
        })
    }

    private fun allWordHandler(): Handler<RoutingContext> {
        val eb: EventBus = vertx.eventBus()
        return Handler({ rc ->
            eb.send<Any>(EventSourceAddress.wordAll, "") { reply ->
                val msg = reply.result()
                val st = msg.body() as String
                rc.response().putHeader("content-type", "application/json").end(st)
            }
        })
    }

    private fun queryWordHandler(): Handler<RoutingContext> {
        val eb: EventBus = vertx.eventBus()
        return Handler({ rc ->
            val id = rc.request().getParam("id")
            eb.send<Any>(EventSourceAddress.wordQuery, id) { reply ->
                val msg = reply.result()
                val cnt = msg.body() as Int//(Integer) msg..body();
                rc.response().putHeader("content-type", "application/json").end(JsonObject().put(id, cnt).encode())
            }
        })
    }

    private fun queryEventHandler(): Handler<RoutingContext> {
        val eb: EventBus = vertx.eventBus()
        return Handler({ rc ->
            val id = rc.request().getParam("id")
            eb.send<Any>(EventSourceAddress.eventQuery, id) { reply ->
                val msg = reply.result()
                val cnt = msg.body() as Int//(Integer) msg..body();
                rc.response().putHeader("content-type", "application/json").end(JsonObject().put(id, cnt).encode())
            }
        })
    }

}