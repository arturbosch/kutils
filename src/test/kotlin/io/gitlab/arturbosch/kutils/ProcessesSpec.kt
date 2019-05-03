package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.beEmpty
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import java.nio.file.Files
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * @author Artur Bosch
 */
@EnabledIfSystemProperty(named = "os.name", matches = "*Linux*")
class ProcessesSpec : StringSpec({

    "it should start and consume a process" {
        val processStatus = process(
                listOf("ls", "-lA"),
                Files.createTempDirectory("kutils").toFile()
        ).consume()
        val (out, err, status) = processStatus
                .onSuccess { assertTrue(true) }
                .onError { fail("Should execute ls -lA successfully.") }

        status shouldBe 0
        out shouldNot beEmpty()
        err should beEmpty()
        processStatus.getOrNull() shouldNotBe null
        processStatus.getOrThrow { IllegalStateException() } shouldNot beEmpty()
    }
})
