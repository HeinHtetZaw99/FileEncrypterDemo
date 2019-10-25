package com.daniel.encrypter

import android.util.Log
import org.apache.commons.codec.binary.Hex
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class FileListEncrypter(
    private val mode: FileEncrypter.MODE,
    private val listener: FileListEncryptionListener?
) {

    companion object {
        private var INSTANCE: FileListEncrypter? = null
        private const val TAG = "FILE_LIST_ENCRYPTER"

        fun getInstance(): FileListEncrypter {
            if (INSTANCE == null) {
                throw RuntimeException("FileListEncrypter is not initialized")
            } else
                return INSTANCE!!
        }

    }

    fun encryptFiles(fileList: Array<File>) {
        try {
            Log.e(TAG, "********** Encryption of Files List Started **********")
            listener?.onStartEncryptingFilesList()
            val startTime = Calendar.getInstance().timeInMillis
            val totalFileSize: Long = FileUtils.calculateFileSizes(fileList)
            var currentProgress = 0L

            for (file in fileList) {
                val fileName = "${file.name}-enc"
                FileEncrypter.getInstance().encryptFile(file, fileName)
                currentProgress += file.length()
                val currentProcessInPercent = calculatePercentage(currentProgress, totalFileSize)
                Log.e(TAG, "$currentProcessInPercent %")
                listener?.onProgressUpdate(currentProcessInPercent)
            }
            val endTime = Calendar.getInstance().timeInMillis
            listener?.onFinishedEncryptingFilesList()
            printLine(TAG)
            Log.i(
                TAG,
                "\n******  Summary of Operation ( Encryption with $mode to FileList (${fileList.size} file/es with Total File Size ${totalFileSize.toMB()} MB) )   ****** \n " +
                        "\t\t Started @\t\t ${convertLongToTime(startTime)} \n" +
                        "\t\t Ended   @\t\t ${convertLongToTime(endTime)} \n" +
                        "\t\t Elapsed @\t\t ${(endTime - startTime).elapsedTime()} \n"
            )
            printLine(TAG)
            Log.e(TAG, "********** Encryption of Files List Ended **********")
        } catch (ioe: IOException) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "IOE @ Encryption of Files List : ${ioe.message}")
        } catch (oom: OutOfMemoryError) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "OOM @ Encryption of Files List : ${oom.message}")
        } catch (e: Exception) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "E @ Encryption of Files List: ${e.message}")
        }
    }

    fun decryptFiles(fileList: Array<File>) {
        try {
            var currentProgress = 0L
            val totalFileSize = FileUtils.calculateFileSizes(fileList)
            Log.e(TAG, "********** Decryption of Files List Started **********")
            val startTime = Calendar.getInstance().timeInMillis
            for (file in fileList) {
                val fileName = "${file.name}-dec"
                FileEncrypter.getInstance().decryptFile(file, fileName)
                currentProgress += file.length()
                val currentProcessInPercent = calculatePercentage(currentProgress, totalFileSize)
                Log.e(TAG, "$currentProcessInPercent %")
                listener?.onProgressUpdate(currentProcessInPercent)
            }
            val endTime = Calendar.getInstance().timeInMillis
            printLine(TAG)
            Log.i(
                TAG,
                "\n******  Summary of Operation ( Decryption with $mode to FileList (${fileList.size} file/es with Total File Size ${totalFileSize.toMB()} MB)   ****** \n " +
                        "\t\t Started @\t\t ${convertLongToTime(startTime)} \n" +
                        "\t\t Ended   @\t\t ${convertLongToTime(endTime)} \n" +
                        "\t\t Elapsed @\t\t ${(endTime - startTime).elapsedTime()} \n"
            )
            printLine(TAG)
            Log.e(TAG, "********** Decryption of Files List Ended **********")
        } catch (ioe: IOException) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "IOE @ Decryption of Files List: ${ioe.message}")
        } catch (oom: OutOfMemoryError) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "OOM @ Decryption of Files List: ${oom.message}")
        } catch (e: Exception) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "E @ Decryption of Files List: ${e.message}")
        }

    }


    /** Builder Class for building INSTANCE with builder pattern*/
    class Builder {
        private var mode: FileEncrypter.MODE =
            FileEncrypter.MODE.AESBF
        private var encryptDirectory: String = ""
        private var decryptDirectory: String = ""
        private var key: String = ""
        private var ivs: String = ""
        private var listener: FileListEncryptionListener? = null
        private var encryptExtension: ENCRYPT_FILE_EXTENSION =
            ENCRYPT_FILE_EXTENSION.DAT
        private var decryptExtension: DECRYPT_FILE_EXTENSION =
            DECRYPT_FILE_EXTENSION.MP4


        fun withEncryptDirectory(directory: String): Builder {
            this.encryptDirectory = directory
            return this
        }


        fun withEncryptionListener(listener: FileListEncryptionListener?): Builder {
            this.listener = listener
            return this
        }

        fun withDecryptDirectory(directory: String): Builder {
            this.decryptDirectory = directory
            return this
        }

        fun withEncryptExtension(extension: ENCRYPT_FILE_EXTENSION): Builder {
            this.encryptExtension = extension
            return this
        }

        fun withDecryptExtension(extension: DECRYPT_FILE_EXTENSION): Builder {
            this.decryptExtension = extension
            return this
        }

        fun withKey(keyString: String): Builder {
            this.key = keyString
            return this
        }

        fun withIvs(ivsString: String): Builder {
            this.ivs = ivsString
            return this
        }

        fun withMode(mode: FileEncrypter.MODE): Builder {
            this.mode = mode
            return this
        }

        @Throws(Exception::class)
        private fun generateKey(key: String, ivs: String): String {
            val sha256HMAC = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
            sha256HMAC.init(secretKey)
            return String(Hex.encodeHex(sha256HMAC.doFinal(ivs.toByteArray(StandardCharsets.UTF_8))))
        }

        fun build() {
            if (encryptDirectory == "" || decryptDirectory == "" || key == "" || ivs == "") {
                when {
                    encryptDirectory == "" -> throw RuntimeException("Invalid `EncryptedDirectory`")
                    decryptDirectory == "" -> throw RuntimeException("Invalid `DecryptedDirectory`")
                    key == "" -> throw NullPointerException("Invalid `Key`")
                    ivs == "" -> throw NullPointerException("Invalid `ivs`")
                    else -> throw RuntimeException("FileEncrypter is not properly initialized")
                }
            } else {

                val keyByteArray = generateKey(key, ivs).toByteArray()
                val keyB = ByteArray(32)
                val ivsB = ByteArray(16)
                val tempIV = ByteArray(32)

                if (keyByteArray.size == 64) {
                    for (byte in keyByteArray) {
                        if (keyByteArray.indexOf(byte) < 31)
                            keyB[keyByteArray.indexOf(byte)] = byte
                        else {
                            tempIV[keyByteArray.indexOf(byte) - 32] = byte
                        }
                    }
                    for (i in 0..15) {
                        ivsB[i] = tempIV[i]
                    }
                }
                showLogD(TAG, "Key generated : $keyByteArray")
                showLogD(TAG, "KEY For AES generated : $keyB")
                showLogD(TAG, "IVS For AES generated : $ivsB")
                INSTANCE =
                    FileListEncrypter(
                        mode,
                        listener
                    )

                FileEncrypter.Builder()
                    .withEncryptDirectory(encryptDirectory)
                    .withDecryptDirectory(decryptDirectory)
                    .withEncryptionListener(null)
                    .withKey(key)
                    .withIvs(ivs)
                    .withMode(mode)
                    .build()

            }
        }
    }


    private fun calculatePercentage(currentProgress: Long, totalSize: Long) =
        ((currentProgress.toDouble() / totalSize.toDouble()) * 100).toInt()

    enum class ENCRYPT_FILE_EXTENSION(val extension: String) {
        DAT(".dat"),
        TXT(".txt"),
        EXO(".exo")


    }

    enum class DECRYPT_FILE_EXTENSION(val extension: String) {
        DAT(".dat"),
        TXT(".txt"),
        EXO(".exo"),
        MP4(".mp4"),
        MP3(".mp3"),
    }

}