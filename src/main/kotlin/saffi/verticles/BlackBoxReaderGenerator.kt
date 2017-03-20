package saffi.verticles

import io.vertx.core.Future
import saffi.dataevent.DataStreamHelper

import java.io.IOException
import java.io.InputStream


class BlackBoxReaderGenerator(command: String) : ReaderGenerator() {
    private val command = "./generator-linux-amd64"
    private val streamLineReader: DataStreamHelper = DataStreamHelper(blackBoxInputStream)
//
//    init {
//        this.command = command
//    }

    override val line: String?
        @Throws(IOException::class)
        get() = streamLineReader.line

    override fun start(started: Future<Void>) {
        started.complete()
    }

    private // todo notify, respawn - it should be done by the parent
    val blackBoxInputStream: InputStream
        get() {
            try {
                val process = Runtime.getRuntime().exec(command)
                if (!process.isAlive) {
                    throw RuntimeException("process execute: " + command)
                }
                return process.inputStream
            } catch (e: IOException) {
                val message = "spawn failed:" + command
                throw RuntimeException(message, e)
            }

        }


    public override fun stop(stopped: Future<Void>) {
        // todo - Kill the process  ?
        stopped.complete()
    }

}
