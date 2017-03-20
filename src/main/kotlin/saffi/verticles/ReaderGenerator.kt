package saffi.verticles

import io.vertx.core.Future
import saffi.helper.StreamLineReader


abstract class ReaderGenerator : StreamLineReader {

    internal abstract fun start(started: Future<Void>)

    internal abstract fun stop(stopped: Future<Void>)

}
