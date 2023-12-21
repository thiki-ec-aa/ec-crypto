package net.thiki.crypt

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.Callable
import kotlin.io.path.pathString


/**
 *
 */
@CommandLine.Command(
    name = "CrypterApp",
    mixinStandardHelpOptions = true,
    version = ["1.0.0"],
    description = [""],
    subcommands = [
        EncryptCommand::class,
        DecryptCommand::class,
        InitKeysCommand::class,
    ]

)
class CrypterCommand : Callable<Int> {

//    @CommandLine.Parameters(index = "0", description = ["init-keys, encrypt, decrypt"])
//    lateinit var command: String

    init {
        logger.info("initial CrypterApp")
    }
    override fun call(): Int {
        logger.warn("must specify command: init-keys, encrypt, decrypt")
        return -1
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CrypterCommand::class.java)
    }

}
@CommandLine.Command(name = "init-keys", description = ["initial the key pair."], mixinStandardHelpOptions = true )
class InitKeysCommand : Callable<Int> {
    @CommandLine.Option(names = ["--public-key", "-pu"], description = ["public key file"])
    lateinit var publicKeyFile: String

    @CommandLine.Option(names = ["--private-key", "-pr"], description = ["private key file"])
    lateinit var privateKeyFile: String

    @CommandLine.Option(names = ["--password"], arity = "0..1", interactive = true, defaultValue = "", description = ["password to encrypt the private key file"])
    lateinit var password: CharArray

    override fun call(): Int {
        val crypter = Crypter()
        crypter.initKeys(publicKeyFile, privateKeyFile, password)
        return 0
    }
}

@CommandLine.Command(name = "encrypt", description = ["zip and encrypt the folder into zip file."], mixinStandardHelpOptions = true )
class EncryptCommand : Callable<Int> {

    @CommandLine.Option(names = ["--public-key", "-pu"], description = ["public key file"])
    lateinit var publicKeyFile: String

    @CommandLine.Option(names = ["--zip-file"], description = ["zip file name"])
    lateinit var zipFileName: String

    @CommandLine.Option(names = ["--bundle-folder"], description = ["bundle folder name"])
    lateinit var bundleFolderPath: String

    @CommandLine.Option(names = ["--prefix"], defaultValue = "bd-", description = ["outer zip file name prefix"])
    lateinit var prefix: String

    override fun call(): Int {
        val crypter = Crypter()
        val r = java.security.SecureRandom()
        val l = r.nextLong()
        val t = Instant.now().toEpochMilli()
        val token: String = "$l:$t"

        val encodedMessage = crypter.encrypt(token, publicKeyFile)
        FileOutputStream("token.txt").use { fos -> fos.write(encodedMessage.toByteArray()) }
        // zip the files with passwd
        FileZipper(zipFileName, l.toString().toCharArray()).zipFolder(bundleFolderPath)
        // wrap the zip file and token.txt into another zip file.
        FileZipper(outerZipFileName(zipFileName, prefix)).zipFiles("token.txt", zipFileName)
        File(zipFileName).delete()
        return 0
    }

    /**
     * "bd-$zipFileName"
     */
    fun outerZipFileName(zipFileName: String, prefix: String): String {
        val path = Paths.get(zipFileName)
        return path.parent.pathString + "/" + prefix + path.fileName
    }
}

@CommandLine.Command(name = "decrypt", description = ["decrypt the zip file."], mixinStandardHelpOptions = true )
class DecryptCommand : Callable<Int> {
    @CommandLine.Option(names = ["--target"], description = ["bundle folder name"])
    lateinit var target: String

    @CommandLine.Option(names = ["--private-key", "-pr"], description = ["private key file"])
    lateinit var privateKeyFile: String

    @CommandLine.Option(names = ["--password"], arity = "0..1", interactive = true, description = ["if private key file is a zip, need password."])
    lateinit var password: CharArray

    @CommandLine.Option(names = ["--zip-file"], description = ["zip file name"])
    lateinit var zipFileName: String

    @CommandLine.Option(names = ["--prefix"], defaultValue = "bd-", description = ["outer zip file name prefix"])
    lateinit var prefix: String
    /**
     * "bd-$zipFileName"
     */
    fun innerZipFileName(zipFileName: String, prefix: String): String {
        val path = Paths.get(zipFileName)
        //bd-bundles.zip -> bundles.zip
        return path.fileName.pathString.substring(prefix.length)
    }

    override fun call(): Int {
        FileZipper(zipFileName).extractTo(target)
        val bytes = FileInputStream("$target/token.txt").use { fis -> fis.readBytes() }
        val crypter = Crypter()
        val token = crypter.decrypt(
            bytes.toString(StandardCharsets.UTF_8),
            privateKeyFile,
            password
        )

        val (l, t) = token.split(":").let {
            Pair(
                it[0],
                it[1].toLong()
            )
        }
        if (Instant.ofEpochMilli(t).isBefore(Instant.now().minus(1, ChronoUnit.HOURS))) {
            // token is expired
            throw IllegalArgumentException("token is expired")
        }
        val innerZipFileName = innerZipFileName(zipFileName, prefix )
        FileZipper("$target${File.separator}$innerZipFileName", l.toCharArray()).extractTo(target)

        //cleaning
        File("$target${File.separator}$innerZipFileName").delete()
        File("$target${File.separator}token.txt").delete()

        return 0
    }
}
