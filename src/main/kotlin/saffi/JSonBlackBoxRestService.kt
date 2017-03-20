package saffi

import io.vertx.core.*
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.logging.LoggerFactory
import saffi.helper.VertXDeploymentOptionsFactory
import saffi.helper.VertXDeploymentOptionsFactory.getOptions
import saffi.verticles.JSONPump
import saffi.verticles.JSONPumpAddress

class JSonBlackBoxRestService : AbstractVerticle() {
    internal var logger = LoggerFactory.getLogger(JSONPump::class.java)
    private var consoleOut: MessageConsumer<Any>? = null

    override fun start(started: Future<Void>) {

        val fut1 = Future.future<Void>()
        vertx.deployVerticle("saffi.verticles.EventSource",
                getOptions(this)) { res ->
            if (res.succeeded()) {
                eventSourceId = res.result()
                println("Deployment id is: " + res.result())
                fut1.complete()
            } else {
                val msg = "Deployment failed!"
                println(msg)
                fut1.fail(msg)
            }
        }


        fut1.setHandler { v ->
            val options = DeploymentOptions()
            options.config = config()
            vertx.deployVerticle("saffi.verticles.RestService", options) { res ->
                if (res.succeeded()) {
                    restServiceId = res.result()
                    println("Deployment id is: " + res.result())
                    started.complete()
                } else {
                    val msg = "Deployment failed!"
                    println(msg)
                    started.fail(msg)
                }
            }
        }

        consoleOut = vertx.eventBus().consumer<Any>(JSONPumpAddress.broadcast) { message -> println("I have received a message: " + message.body()) }
    }

    override fun stop(stopped: Future<Void>) {
        val fut1 = Future.future<Void>()
        val fut2 = Future.future<Void>()
        val fut3 = Future.future<Void>()

        consoleOut!!.unregister(fut3.completer())

        println("Undeploy id is: " + restServiceId!!)
        vertx.undeploy(restServiceId, fut1.completer())

        println("Undeploy id is: " + eventSourceId!!)
        vertx.undeploy(eventSourceId, fut2.completer())

        CompositeFuture.join(fut1, fut2, fut3).setHandler { v ->
            println("Undeployed done")
            stopped.complete()
        }
    }

    companion object {
        private var eventSourceId: String? = null
        private var restServiceId: String? = null

        @JvmStatic fun main(args: Array<String>) {

            val vertx = Vertx.vertx()
            val options = VertXDeploymentOptionsFactory.testOptions
            vertx.deployVerticle("saffi.JSonBlackBoxRestService", options)
        }
    }
}
