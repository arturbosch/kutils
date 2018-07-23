package io.gitlab.arturbosch.kutils

import java.io.File

/**
 * @author Artur Bosch
 */
data class ProcessStatus(val out: List<String>, val err: List<String>, val code: Int) {

	inline infix fun onSuccess(block: (List<String>) -> Unit): ProcessStatus {
		if (code == 0) {
			block.invoke(out)
		}
		return this
	}

	inline infix fun onError(block: (List<String>) -> Unit): ProcessStatus {
		if (code != 0) {
			block.invoke(err)
		}
		return this
	}

}

fun process(args: List<String>, directory: File = File(".")): Process {
	check(directory.exists()) { "'$directory' must exist." }
	return ProcessBuilder()
			.command(args)
			.directory(directory)
			.start()
}

fun Process.consume(): ProcessStatus {
	val out = this.inputStream.bufferedReader().readLines()
	val err = this.errorStream.bufferedReader().readLines()
	val code = this.waitFor()
	this.destroy()
	return ProcessStatus(out, err, code)
}
