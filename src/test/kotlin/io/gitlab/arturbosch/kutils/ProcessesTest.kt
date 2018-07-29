package io.gitlab.arturbosch.kutils

import org.junit.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * @author Artur Bosch
 */
class ProcessesTest {

	@Test
	@EnabledIfSystemProperty(named = "os.name", matches = "*Linux*")
	fun successful() {
		val (out, err, status) =
				process(listOf("ls", "-lA"), Files.createTempDirectory("kutils").toFile())
						.consume()
						.onSuccess { assertTrue(true) }
						.onError { fail("Should execute ls -lA successfully.") }

		assertEquals(status, 0)
		assertTrue(out.isNotEmpty())
		assertTrue(err.isEmpty())
	}
}
