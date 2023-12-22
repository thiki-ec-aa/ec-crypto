package net.thiki.crypt

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.io.path.pathString


class Crypter {

    fun test(){

    }

    /**
     * @return publicKey
     */
    fun readKeyFromFile(path: String, isPublicKey: Boolean = true): Key {
        val keyFile = File(path)
        if (!keyFile.exists()) {
            throw RuntimeException("key file not found, path=$keyFile")
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

    fun initKeys(
        publicKeyFile: String = "public.key",
        privateKeyFile: String = "private.key",
        password: CharArray? = null,
    ) {
        // init the key pair
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        val pair = generator.generateKeyPair()
        writeKeyToFile(publicKeyFile, pair.public.encoded)
        writeKeyToFile(privateKeyFile, pair.private.encoded)

        if (password != null && password.isNotEmpty()) {
            FileZipper("${privateKeyFile}.zip", password).zipFiles(privateKeyFile)
            File(privateKeyFile).delete()
        }
    }

    fun writeKeyToFile(fileName: String, keyEncoded: ByteArray ) {
        val path = Paths.get(fileName)
        val parent = path.parent
        if (parent != null && !parent.toFile().exists()){
            parent.toFile().mkdirs()
        }
        FileOutputStream(fileName).use { fos -> fos.write(keyEncoded) }
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

    fun decrypt(encodedMessage: String, keyFile: String, password: CharArray? = null): String {
        val decryptCipher = Cipher.getInstance("RSA")
        var realKeyFile = keyFile
        if (keyFile.endsWith(".zip")){
            // extract the key file
            if (password != null && password.isNotEmpty()){
                realKeyFile = keyFile.substring(0, keyFile.length - ".zip".length)
                val path = Paths.get(realKeyFile)
                val parentPath =
                    if (path.parent != null && path.parent.toFile().exists()){
                        path.parent.pathString
                    }else{
                        "."
                    }
                val fileName = path.fileName.pathString
                logger.info("keyFile: $keyFile, fileName: $fileName, parentPath: $parentPath")
                FileZipper(keyFile, password).extractFile(fileName, parentPath)
            }else{
                throw IllegalArgumentException("zip key file must input the password.")
            }
        }

        val key = readKeyFromFile(realKeyFile, false)
        decryptCipher.init(Cipher.DECRYPT_MODE, key)

        val decodedBA = Base64.getDecoder().decode(encodedMessage)
        val decryptedMsg = decryptCipher.doFinal(decodedBA).toString(StandardCharsets.UTF_8)

        if (keyFile.endsWith(".zip")){
            //delete the raw key file.
            File(realKeyFile).delete()
        }
        return decryptedMsg
    }
    companion object {
        private val logger = LoggerFactory.getLogger(DecryptCommand::class.java)
    }
}