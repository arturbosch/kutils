package io.gitlab.arturbosch.kutils

import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class HttpFileUpload constructor(requestURL: String,
								 private val charset: String,
								 headers: Map<String, String>) {

	companion object {
		private const val LINE_FEED = "\r\n"
	}

	private val boundary: String = "===" + System.currentTimeMillis() + "==="
	private val httpConn: HttpURLConnection
	private val outputStream: OutputStream
	private val writer: PrintWriter

	init {
		val url = URL(requestURL)
		httpConn = url.openConnection() as HttpURLConnection
		httpConn.useCaches = false
		httpConn.doOutput = true
		httpConn.doInput = true
		httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary)
		headers.forEach { httpConn.setRequestProperty(it.key, it.value) }
		outputStream = httpConn.outputStream
		writer = PrintWriter(OutputStreamWriter(outputStream, charset), true)
	}

	@Throws(IOException::class)
	fun addFormField(name: String, value: String): HttpFileUpload {
		writer.append("--").append(boundary).append(LINE_FEED)
		writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED)
		writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE_FEED)
		writer.append(LINE_FEED)
		writer.append(value).append(LINE_FEED)
		writer.flush()
		return this
	}

	@Throws(IOException::class)
	fun addFilePart(fieldName: String, uploadFile: Path): HttpFileUpload {
		val fileName = uploadFile.fileName.toString()
		writer.append("--").append(boundary).append(LINE_FEED)
		writer.append("Content-Disposition: form-data; name=\"")
				.append(fieldName).append("\"; filename=\"")
				.append(fileName).append("\"").append(LINE_FEED)
		writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED)
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
		writer.append(LINE_FEED)
		writer.flush()

		Files.copy(uploadFile, outputStream)
		outputStream.flush()

		writer.append(LINE_FEED)
		writer.flush()
		return this
	}

	/**
	 * Completes the request and receives response from the server.

	 * @return a list of Strings as response in case the server returned status OK,
	 * otherwise an exception is thrown.
	 */
	@Throws(IOException::class)
	fun execute(): Response {
		writer.append(LINE_FEED).flush()
		writer.append("--").append(boundary).append("--").append(LINE_FEED)
		writer.close()

		val status = httpConn.responseCode
		return if (status == HttpURLConnection.HTTP_OK) {
			Response.Success(status, with(httpConn.inputStream.bufferedReader()) {
				val response = lineSequence().toList()
				response.forEach(::println)
				close()
				httpConn.disconnect()
				response
			})
		} else {
			Response.Failure(status, IOException("Server returned non-OK status: " + status))
		}
	}

	/**
	 * Represents a result. Can be successful with a content (list of strings) or failure with an exception.
	 */
	sealed class Response(val status: Int) {

		abstract fun success(): Boolean
		abstract fun failure(): Boolean
		abstract fun body(): List<String>
		abstract fun errorBody(): Exception

		class Success(status: Int, val content: List<String>) : Response(status) {
			override fun success(): Boolean = true
			override fun failure(): Boolean = false
			override fun body(): List<String> = content
			override fun errorBody(): Exception = throw IllegalStateException("No error for success response.")
		}

		class Failure(status: Int, val error: IOException) : Response(status) {
			override fun success(): Boolean = false
			override fun failure(): Boolean = true
			override fun body(): List<String> = throw error
			override fun errorBody(): Exception = error
		}
	}
}
