package com.daniel.encrypter

import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class BlowFishUtils(
    private val keyB: ByteArray,
    private val listener: FileEncryptionListener?
) {


    companion object {
        private var INSTANCE: BlowFishUtils? = null
        private const val ALGORITHM = "Blowfish"
        private const val TAG = "BLOW_FISH_TAG"
        fun getInstance(): BlowFishUtils {
            if (INSTANCE != null) {
                return INSTANCE!!
            } else throw RuntimeException("BlowFishUtils has not been initialized")
        }
    }


    fun encrypt(inputFile: ByteArray): ByteArray? {
        return try {
            doCrypto(Cipher.ENCRYPT_MODE, inputFile)
        } catch (ome: OutOfMemoryError) {
            Log.e(TAG, "Out of Memory Problem encountered @ Encryption @ BF : \t${ome.message}")
            listener?.onCancelled("Encrypting Operation Failed")
            null
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Exception encountered @ Encryption @ BF : \t${e.message}")
            listener?.onCancelled("Encrypting Operation Failed")
            null
        }

    }

    @Throws(Exception::class)
    fun decrypt(inputFile: ByteArray): ByteArray? {
        return try {
            doCrypto(Cipher.DECRYPT_MODE, inputFile)
        } catch (ome: OutOfMemoryError) {
            Log.e(TAG, "Out of Memory Problem encountered : \t${ome.message}")
            listener?.onCancelled("Decrypting Operation Failed")
            null
        }catch (e: java.lang.Exception) {
            Log.e(TAG, "Exception encountered @ Decryption @ BF : \t${e.message}")
            listener?.onCancelled("Decrypting Operation Failed")
            null
        }

    }


    @Throws(Exception::class)
    private fun doCrypto(
        cipherMode: Int, inputFile: ByteArray
    ): ByteArray? {

        val secretKey = SecretKeySpec(keyB,
            ALGORITHM
        )
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(cipherMode, secretKey)
        return cipher.doFinal(inputFile)
    }


    /** Builder Class for building INSTANCE with builder pattern*/
    class Builder {
        private var key: ByteArray? = null

        private var listener: FileEncryptionListener? = null


        fun withEncryptionListener(listener: FileEncryptionListener?): Builder {
            this.listener = listener
            return this
        }

        fun withKey(key: ByteArray): Builder {
            this.key = key
            return this
        }

        fun build() {
            if (key == null ) {
                when {
                    key == null -> throw NullPointerException("Invalid `Key`")
                    else -> throw RuntimeException("BlowFishUtils is not properly initialized")
                }
            } else {

                INSTANCE =
                    BlowFishUtils(
                        key!!,
                        listener
                    )
            }
        }
    }

}