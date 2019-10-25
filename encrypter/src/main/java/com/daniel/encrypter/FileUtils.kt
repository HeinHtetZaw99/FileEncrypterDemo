package com.daniel.encrypter

import android.os.Environment
import android.util.Log
import org.apache.commons.io.IOUtils.EOF
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.*


object FileUtils {

    private const val TAG = "FILE_UTILS"

    /* Checks if external storage is available for read and write */
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /* Checks if external storage is available to at least read */
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    @Throws(Exception::class)
    fun createFile(dirPath: String, fileName: String) = File(
        Environment.getExternalStorageDirectory()
            .absolutePath + File.separator + dirPath,
        fileName
    )


    fun readFileFromDisk(inputFile: File) = BufferedInputStream(FileInputStream(inputFile))

    fun writeFileToDisk(
        path: String,
        fileName: String,
        fileToWrite: ByteArray,
        listener: FileEncryptionListener?
    ) {
        val dir =
            File(Environment.getExternalStorageDirectory().absolutePath + File.separator + path)
        dir.mkdirs()
        val file = File(dir, fileName)

        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(fileToWrite)
            fileOutputStream.close()
            listener?.onFinished()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            listener?.onFailedWritingFile(e.message!!)
            Log.e(
                TAG,
                "******* File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the manifest?"
            )
        } catch (e: IOException) {
            e.printStackTrace()
            listener?.onFailedWritingFile(e.message!!)
            Log.e(TAG, e.stackTrace.toString())
        }
    }


    /**
     * Reads the contents of a file into a byte array.
     * The file is always closed.
     *
     * @param file the file to read, must not be `null`
     * @return the file contents, never `null`
     * @throws IOException in case of an I/O error
     * @since 1.1
     */
    @Throws(IOException::class, OutOfMemoryError::class)
    fun readFileToByteArray(file: File): InputStream? {
        var inputStream: InputStream? = null
        try {
            inputStream =
                openBufferInputStream(file)
            return inputStream // Do NOT use file.length() - see IO-453
        } finally {
            closeQuietly(inputStream)
        }
    }


    fun deleteDir(path: String) {
        val fileList = readFilesFromDirectory(path)


        try {
            val fileCounts = fileList!!.size
            val totalFileSize = calculateFileSizes(fileList).toMB()
            for (file in fileList) {
                file.delete()
            }
            Log.e(
                TAG,
                "All files Deleted under the directory : $path \t\t\t\t ( $fileCounts files ) with total file size : $totalFileSize MB"
            )
        } catch (npe: java.lang.NullPointerException) {
            Log.e(TAG, "NullPointerException at deleteDir(path : String)")
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Exception Thrown by deleteDir(path : String) : ${e.message}")
        }
    }

    fun calculateFileSizes(fileList: Array<File>): Long {
        var totalFileSize = 0L
        for (file in fileList) {
            totalFileSize += file.length()
        }
        return totalFileSize
    }

    fun readFilesFromDirectory(path: String): Array<File>? {
        try {
            val directory = File(path)
            return if (directory.isDirectory) {
                val files = directory.listFiles()
                showLogD("Files List Found : $files ")
                showLogD("Number of Files Found : ${files!!.size} ")
                files
            } else null
        } catch (fnfe: FileNotFoundException) {
            showLogE("FNFE @ reading Files from storage : $fnfe")
            return null
        } catch (npe: NullPointerException) {
            showLogE("NPE @ reading Files from storage : $npe")
            return null
        } catch (e: java.lang.Exception) {
            showLogE("E @ reading Files from storage : $e")
            return null
        }
    }

    /**
     * Closes a `Closeable` unconditionally.
     *
     *
     * Equivalent to [Closeable.close], except any exceptions will be ignored. This is typically used in
     * finally blocks.
     *
     *
     * Example code:
     *
     * <pre>
     * Closeable closeable = null;
     * try {
     * closeable = new FileReader(&quot;foo.txt&quot;);
     * // process closeable
     * closeable.close();
     * } catch (Exception e) {
     * // error handling
     * } finally {
     * IOUtils.closeQuietly(closeable);
     * }
    </pre> *
     *
     *
     * Closing all streams:
     *
     * <pre>
     * try {
     * return IOUtils.copy(inputStream, outputStream);
     * } finally {
     * IOUtils.closeQuietly(inputStream);
     * IOUtils.closeQuietly(outputStream);
     * }
    </pre> *
     *
     * @param closeable the objects to close, may be null or already closed
     * @since 2.0
     */
    private fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (ioe: IOException) {
            // ignore
            Log.d(TAG, ioe.stackTrace.toString())
        }

    }

    /**
     * Gets the contents of an `InputStream` as a `byte[]`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedInputStream`.
     *
     * @param input the `InputStream` to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    @Throws(IOException::class)
    fun toByteArray(input: InputStream): ByteArray {
        val output = ByteArrayOutputStream()
        copy(input, output)
        return output.toByteArray()
    }

    /**
     * Copies bytes from an `InputStream` to an
     * `OutputStream`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedInputStream`.
     *
     *
     * Large streams (over 2GB) will return a bytes copied value of
     * `-1` after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the `copyLarge(InputStream, OutputStream)` method.
     *
     * @param input the `InputStream` to read from
     * @param output the `OutputStream` to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.1
     */
    @Throws(IOException::class)
    fun copy(input: InputStream, output: OutputStream): Int {
        val count = copyLarge(input, output)
        return if (count > Integer.MAX_VALUE) {
            -1
        } else count.toInt()
    }

    /**
     * Copies bytes from a large (over 2GB) `InputStream` to an
     * `OutputStream`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedInputStream`.
     *
     *
     * The buffer size is given by [.DEFAULT_BUFFER_SIZE].
     *
     * @param input the `InputStream` to read from
     * @param output the `OutputStream` to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 1.3
     */
    @Throws(IOException::class)
    fun copyLarge(input: InputStream, output: OutputStream): Long {
        return copy(
            input,
            output,
            DEFAULT_BUFFER_SIZE
        )
    }

    /**
     * Copies bytes from an `InputStream` to an `OutputStream` using an internal buffer of the
     * given size.
     *
     *
     * This method buffers the input internally, so there is no need to use a `BufferedInputStream`.
     *
     *
     *
     * @param input the `InputStream` to read from
     * @param output the `OutputStream` to write to
     * @param bufferSize the bufferSize used to copy from the input to the output
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.5
     */
    @Throws(IOException::class)
    fun copy(input: InputStream, output: OutputStream, bufferSize: Int): Long {
        return copyLarge(
            input,
            output,
            ByteArray(bufferSize)
        )
    }

    /**
     * Copies bytes from a large (over 2GB) `InputStream` to an
     * `OutputStream`.
     *
     *
     * This method uses the provided buffer, so there is no need to use a
     * `BufferedInputStream`.
     *
     *
     *
     * @param input the `InputStream` to read from
     * @param output the `OutputStream` to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since 2.2
     */
    @Throws(IOException::class)
    fun copyLarge(input: InputStream, output: OutputStream, buffer: ByteArray): Long {
        var count: Long = 0
        val n: Int = input.read(buffer)
        while (EOF != n) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }

    /**
     * Opens a [FileInputStream] for the specified file, providing better
     * error messages than simply calling `new FileInputStream(file)`.
     *
     *
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     *
     *
     * An exception is thrown if the file does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be read.
     *
     * @param file the file to open for input, must not be `null`
     * @return a new [FileInputStream] for the specified file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException           if the file object is a directory
     * @throws IOException           if the file cannot be read
     * @since 1.3
     */
    @Throws(IOException::class)
    fun openBufferInputStream(file: File): BufferedInputStream {
        if (file.exists()) {
            if (file.isDirectory) {
                throw IOException("File '$file' exists but is a directory")
            }
            if (!file.canRead()) {
                throw IOException("File '$file' cannot be read")
            }
        } else {
            throw FileNotFoundException("File '$file' does not exist")
        }
        return BufferedInputStream(FileInputStream(file))
    }

}