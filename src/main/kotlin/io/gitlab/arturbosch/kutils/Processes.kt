package io.gitlab.arturbosch.kutils

import java.io.File

/**
 * Data class for result of a process execution.
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

/**
 * Spawns a process with given arguments. A working directory is optional.
 */
fun process(args: List<String>, directory: File = File(".")): Process {
    check(directory.exists()) { "'$directory' must exist." }
    return ProcessBuilder()
            .command(args)
            .directory(directory)
            .start()
}

/**
 * Helper function to consume a process e.g. blocking until input and error streams
 * are fully read.
 */
fun Process.consume(): ProcessStatus {
    val out = this.inputStream.bufferedReader().readLines()
    val err = this.errorStream.bufferedReader().readLines()
    val code = this.waitFor()
    this.destroy()
    return ProcessStatus(out, err, code)
}
