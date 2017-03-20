package saffi

import io.vertx.core.Future
import org.junit.Test

class TestFuture {
    @Test
    fun testLearnFuture() {
        val cnt = intArrayOf(0)
        val start = Future.future<Void>()
        val fut1 = Future.future<Void>()
        val fut2 = Future.future<Void>()
        val fut3 = Future.future<Void>()

        fut1.setHandler {
            cnt[0] += 1
            fut2.complete()
        }
        fut2.setHandler {
            cnt[0] *= 2
            fut3.complete()
        }
        fut3.setHandler {
            cnt[0] *= cnt[0]
            start.complete()
        }
        fut1.complete()
        if (cnt[0] != 4) {
            throw RuntimeException("" + cnt[0])
        }
    }
}
