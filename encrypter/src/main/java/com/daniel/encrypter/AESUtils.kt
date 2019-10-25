package com.daniel.encrypter

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESUtils(
    private val keyB: ByteArray,
    private val ivsB: ByteArray,
    private val listener: FileEncryptionListener?
) {
    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private var INSTANCE: AESUtils? = null
        private const val TAG = "AES_TAG"
        fun getInstance(): AESUtils {
            if (INSTANCE != null) {
                return INSTANCE!!
            } else throw RuntimeException("AESUtils has not been initialized")
        }
    }


    fun encrypt(inputFile: ByteArray): ByteArray? {
        return try {
            doCrypto(Cipher.ENCRYPT_MODE, inputFile)
        } catch (ome: OutOfMemoryError) {
            showLogE(
                TAG,
                "Out of Memory Problem encountered  @ Encryption with AES: \t${ome.message}"
            )
            listener?.onCancelled("Encrypting Operation Failed")
            null
        } catch (e: java.lang.Exception) {
            showLogE(TAG, "Exception encountered @ Encryption @ AES : \t${e.message}")
            listener?.onCancelled("Encrypting Operation Failed")
            null
        }
    }

    fun decrypt(inputFile: ByteArray): ByteArray? {
        return try {
            doCrypto(Cipher.DECRYPT_MODE, inputFile)
        } catch (ome: OutOfMemoryError) {
            showLogE(TAG, "Out of Memory Problem encountered @ AES : \t${ome.message}")
            listener?.onCancelled("Decrypting Operation Failed")
            null
        } catch (e: java.lang.Exception) {
            showLogE(TAG, "Exception encountered @ Decryption @ AES : \t${e.message}")
            listener?.onCancelled("Decrypting Operation Failed")
            null
        }

    }


    @Throws(Exception::class)
    private fun doCrypto(
        cipherMode: Int, inputFile: ByteArray
    ): ByteArray? {

        val secretKey = SecretKeySpec(
            keyB,
            ALGORITHM
        )
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivSpec = IvParameterSpec(ivsB)
        cipher.init(cipherMode, secretKey, ivSpec)
        return cipher.doFinal(inputFile)
    }


    /** Builder Class for building INSTANCE with builder pattern*/
    class Builder {
        private var key: ByteArray? = null
        private var ivs: ByteArray? = null
        private var listener: FileEncryptionListener? = null


        fun withEncryptionListener(listener: FileEncryptionListener?): Builder {
            this.listener = listener
            return this
        }


        fun withKey(key: ByteArray): Builder {
            this.key = key
            return this
        }

        fun withIvs(ivs: ByteArray): Builder {
            this.ivs = ivs
            return this
        }


        fun build() {
            if (key == null || ivs == null ) {
                when {
                    key == null -> throw NullPointerException("Invalid `Key`")
                    ivs == null -> throw NullPointerException("Invalid `ivs`")
                    else -> throw RuntimeException("AESUtils is not properly initialized")
                }
            } else {


                INSTANCE =
                    AESUtils(
                        key!!,
                        ivs!!,
                        listener
                    )
            }
        }
    }
}