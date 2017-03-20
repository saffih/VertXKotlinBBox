package saffi.helper

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject

import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

object VertXDeploymentOptionsFactory {
    fun getOptions(verticle: AbstractVerticle): DeploymentOptions {
        var config = verticle.config()
        if (config.isEmpty) {
            config = readConfig()
        }

        val options = DeploymentOptions()
        options.config = config
        return options
    }

    val testOptions: DeploymentOptions
        get() {
            val config = readConfig()

            val options = DeploymentOptions()
            options.config = config
            return options
        }

    private fun readConfig(): JsonObject {
        val runDir = System.getProperty("user.dir")
        val path = "src/main/conf/blackbox.json"
        val jsonConf = readFile(path, StandardCharsets.UTF_8)
        return JsonObject(jsonConf)
    }

    private fun readFile(path: String, encoding: Charset): String {
        try {
            var encoded = ByteArray(0)
            encoded = Files.readAllBytes(Paths.get(path))
            return String(encoded)
        } catch (e: IOException) {
            throw RuntimeException("can't find config at  " + path, e)
        }

    }
}
