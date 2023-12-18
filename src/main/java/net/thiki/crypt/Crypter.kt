package net.thiki.crypt

import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


class Crypter {

    fun test(){

    }

    /**
     * @return publicKey
     */
    fun readKeyFromFile(path: String, isPublicKey: Boolean = true): Key {
        val keyFile = File(path)
        if (!keyFile.exists()) {
            throw RuntimeException("public key file not found")
        }
        val keyBytes = Files.readAllBytes(keyFile.toPath())
        val keyFactory = KeyFactory.getInstance("RSA")
        if (isPublicKey){
            val keySpec: EncodedKeySpec = X509EncodedKeySpec(keyBytes)
            return keyFactory.generatePublic(keySpec)
        }else{
            val keySpec: EncodedKeySpec = PKCS8EncodedKeySpec(keyBytes)
            return keyFactory.generatePrivate(keySpec)
        }
    }

    fun initKeys(publicKeyFile: String = "public.key", privateKeyFile: String = "private.key") {
        // init the key pair
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        val pair = generator.generateKeyPair()

        println("public.algorithm:" + pair.public.algorithm)
        println("public.format:" + pair.public.format)
        println("public.encoded:" + java.lang.String(pair.public.encoded, "UTF-8"))
        println("private.algorithm:" + pair.private.algorithm)
        println("private.format:" + pair.private.format)
        println("private.encoded:" + java.lang.String(pair.private.encoded, "UTF-8"))

        writeKeyToFile(publicKeyFile, pair.public.encoded)
        writeKeyToFile(privateKeyFile, pair.private.encoded)
    }

    fun writeKeyToFile(path: String, keyEncoded: ByteArray ) {
        FileOutputStream(path).use { fos -> fos.write(keyEncoded) }
    }

    /**
     * @return
     */
    fun encrypt(text: String, publicKeyFile: String): String {
        //encrypt
        val publicKey = readKeyFromFile(publicKeyFile)

        val encryptCipher = Cipher.getInstance("RSA")
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBA = encryptCipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
        val encodedMessage: String = Base64.getEncoder().encodeToString(encryptedBA)
        return encodedMessage
    }

    fun decrypt(encodedMessage: String, keyFile: String): String {
        val decryptCipher = Cipher.getInstance("RSA")
        val key = readKeyFromFile(keyFile, false)
        decryptCipher.init(Cipher.DECRYPT_MODE, key)

        val decodedBA = Base64.getDecoder().decode(encodedMessage)
        val decryptedMsg = decryptCipher.doFinal(decodedBA).toString(StandardCharsets.UTF_8)
        return decryptedMsg


    }
}