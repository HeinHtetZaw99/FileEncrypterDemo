package com.daniel.encrypter

import android.util.Log
import com.daniel.encrypter.FileUtils.readFileFromDisk
import com.daniel.encrypter.FileUtils.writeFileToDisk
import org.apache.commons.codec.binary.Hex
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class FileEncrypter private constructor(
    private var encryptDirectory: String,
    private var decryptDirectory: String,
    private val mode: MODE,
    private val listener: FileEncryptionListener?,
    private val encryptExtension: FileListEncrypter.ENCRYPT_FILE_EXTENSION,
    private val decryptExtension: FileListEncrypter.DECRYPT_FILE_EXTENSION
) {


    /** Encryption Methods Starts Here*/
    fun encryptFile(file: File, fileName: String) {
        try {
            val startTime = Calendar.getInstance().timeInMillis

            when (mode) {
                MODE.AES -> {
                    showLogD(TAG, "Encrypted With AES")
                    encryptWithAES(file, "$fileName${encryptExtension.extension}")
                }
                MODE.AESBF -> {
                    showLogD(TAG, "Encrypted With AES + BF")
                    encryptWithBoth(file, "$fileName${encryptExtension.extension}")
                }
                MODE.BF -> {
                    showLogD(TAG, "Encrypted With BF")
                    encryptWithBF(file, "$fileName${encryptExtension.extension}")
                }
            }
            val endTime = Calendar.getInstance().timeInMillis
            Log.i(
                TAG, "\n******  Summary of Operation ( Encryption with $mode )   ****** \n " +
                        "\t\t Started @\t\t ${convertLongToTime(startTime)} \n" +
                        "\t\t Ended   @\t\t ${convertLongToTime(endTime)} \n" +
                        "\t\t Elapsed @\t\t ${(endTime - startTime).elapsedTime()} \n"
            )

        } catch (ioe: IOException) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "IOE @ Encryption : ${ioe.message}")
        } catch (oom: OutOfMemoryError) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "OOM @ Encryption : ${oom.message}")

        } catch (e: Exception) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "E @ Encryption : ${e.message}")
        }
    }


    private fun encryptWithBoth(file: File, fileName: String) {
        listener?.onStartEncryption("Encryption Started")
        val inputFileByteArray = readFileFromDisk(file)
        val encryptedData = BlowFishUtils.getInstance()
            .encrypt(AESUtils.getInstance().encrypt(inputFileByteArray.readBytes())!!)
        listener?.onFinishedEncryption("Encryption Finished")
        listener?.onStartWritingFile("File is being written to disk")
        writeFileToDisk(encryptDirectory, fileName, encryptedData!!, listener)
    }

    private fun encryptWithAES(file: File, fileName: String) {
        listener?.onStartEncryption("Encryption Started")
        val inputFileByteArray = readFileFromDisk(file)
        val encryptedData = AESUtils.getInstance()
            .encrypt(inputFileByteArray.readBytes())
        listener?.onFinishedEncryption("Encryption Finished")
        listener?.onStartWritingFile("File is being written to disk")
        writeFileToDisk(encryptDirectory, fileName, encryptedData!!, listener)
    }

    private fun encryptWithBF(file: File, fileName: String) {
        listener?.onStartEncryption("Encryption Started")
        val inputFileByteArray = readFileFromDisk(file)
        val encryptedData = BlowFishUtils.getInstance()
            .encrypt(inputFileByteArray.readBytes())
        listener?.onFinishedEncryption("Encryption Finished")
        listener?.onStartWritingFile("File is being written to disk")
        writeFileToDisk(encryptDirectory, fileName, encryptedData!!, listener)
    }

    /** Encryption Methods Ends Here*/


    companion object {
        private var INSTANCE: FileEncrypter? = null
        private const val TAG = "FILE_ENCRYPTER"

        fun getInstance(): FileEncrypter {
            if (INSTANCE == null) {
                throw RuntimeException("FileEncrypter is not initialized")
            } else
                return INSTANCE!!
        }

    }


    /** Decryption Methods Starts Here*/

    fun decryptFile(file: File, fileName: String) {
        try {
            val startTime = Calendar.getInstance().timeInMillis

            when (mode) {
                MODE.AES -> {
                    showLogD(TAG, "Decrypted With AES")
                    decryptWithAES(file, "$fileName${decryptExtension.extension}")
                }
                MODE.AESBF -> {
                    showLogD(TAG, "Decrypted With AES + BF")
                    decryptWithBoth(file, "$fileName${decryptExtension.extension}")
                }
                MODE.BF -> {
                    showLogD(TAG, "Decrypted With BF")
                    decryptWithBF(file, "$fileName${decryptExtension.extension}")
                }
            }
            val endTime = Calendar.getInstance().timeInMillis
            Log.i(
                TAG, "\n******  Summary of Operation ( Decryption with $mode )   ****** \n " +
                        "\t\t Started @\t\t ${convertLongToTime(startTime)} \n" +
                        "\t\t Ended   @\t\t ${convertLongToTime(endTime)} \n" +
                        "\t\t Elapsed @\t\t ${(endTime - startTime).elapsedTime()} \n"
            )

        } catch (ioe: IOException) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "IOE @ Decryption : ${ioe.message}")
        } catch (oom: OutOfMemoryError) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "OOM @ Decryption : ${oom.message}")
        } catch (e: Exception) {
            listener?.onCancelled("Operation Failed")
            showLogE(TAG, "E @ Decryption : ${e.message}")
        }

    }


    private fun decryptWithBoth(file: File, fileName: String) {
        listener?.onStartDecryption("Decryption Started")
        val inputFileByteArray = readFileFromDisk(file)
        val decryptedData = AESUtils.getInstance()
            .decrypt(BlowFishUtils.getInstance().decrypt(inputFileByteArray.readBytes())!!)
        listener?.onFinishedEncryption("Decryption Finished")
        listener?.onStartWritingFile("File is being written to disk")
        writeFileToDisk(decryptDirectory, fileName, decryptedData!!, listener)
    }

    private fun decryptWithAES(file: File, fileName: String) {
        listener?.onStartDecryption("Decryption Started")
        val inputFileByteArray = readFileFromDisk(file)
        val decryptedData = AESUtils.getInstance()
            .decrypt(inputFileByteArray.readBytes())
        listener?.onFinishedEncryption("Decryption Finished")
        listener?.onStartWritingFile("File is being written to disk")
        writeFileToDisk(decryptDirectory, fileName, decryptedData!!, listener)
    }

    private fun decryptWithBF(file: File, fileName: String) {
        listener?.onStartDecryption("Decryption Started")
        val inputFileByteArray = readFileFromDisk(file)
        val decryptedData = BlowFishUtils.getInstance()
            .decrypt(inputFileByteArray.readBytes())
        listener?.onFinishedEncryption("Decryption Finished")
        listener?.onStartWritingFile("File is being written to disk")
        writeFileToDisk(decryptDirectory, fileName, decryptedData!!, listener)
    }

    /** Decryption Methods Ends Here*/


    /** Builder Class for building INSTANCE with builder pattern*/
    class Builder {
        private var mode: MODE =
            MODE.AESBF
        private var encryptDirectory: String = ""
        private var decryptDirectory: String = ""
        private var key: String = ""
        private var ivs: String = ""
        private var listener: FileEncryptionListener? = null
        private var encryptExtension: FileListEncrypter.ENCRYPT_FILE_EXTENSION =
            FileListEncrypter.ENCRYPT_FILE_EXTENSION.DAT
        private var decryptExtension: FileListEncrypter.DECRYPT_FILE_EXTENSION =
            FileListEncrypter.DECRYPT_FILE_EXTENSION.MP4


        fun withEncryptDirectory(directory: String): Builder {
            this.encryptDirectory = directory
            return this
        }


        fun withEncryptionListener(listener: FileEncryptionListener?): Builder {
            this.listener = listener
            return this
        }

        fun withDecryptDirectory(directory: String): Builder {
            this.decryptDirectory = directory
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

        fun withMode(mode: MODE): Builder {
            this.mode = mode
            return this
        }

        fun withEncryptExtension(extension: FileListEncrypter.ENCRYPT_FILE_EXTENSION): Builder {
            this.encryptExtension = extension
            return this
        }

        fun withDecryptExtension(extension: FileListEncrypter.DECRYPT_FILE_EXTENSION): Builder {
            this.decryptExtension = extension
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
                    FileEncrypter(
                        encryptDirectory,
                        decryptDirectory,
                        mode,
                        listener,
                        encryptExtension,
                        decryptExtension
                    )

                AESUtils.Builder()
                    .withKey(keyB)
                    .withIvs(ivsB)
                    .withEncryptionListener(listener)
                    .build()


                BlowFishUtils.Builder()
                    .withEncryptionListener(listener)
                    .withKey(keyB)
                    .build()

            }
        }
    }

    enum class MODE {
        AES, BF, AESBF
    }

}

