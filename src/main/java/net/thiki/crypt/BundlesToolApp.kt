package net.thiki.crypt

import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.Callable
import kotlin.system.exitProcess

fun main(args: Array<String>) : Unit = exitProcess(CommandLine(BundlesToolApp()).execute(*args))

/**
 *
 */
@CommandLine.Command(
    name = "BundlesToolApp",
    mixinStandardHelpOptions = true,
    version = ["1.0.0"],
    description = [""]
)
class BundlesToolApp: Callable<Int> {

    @CommandLine.Parameters(index = "0", description = ["init-keys, encrypt, decrypt, help"])
    lateinit var command: String

    override fun call(): Int {
        when(command) {
            "init-keys" -> initKeys()
            "encrypt" -> encrypt()
            "decrypt" -> decrypt()
            "help" -> help()
            else -> throw IllegalArgumentException("command not found: $command")
        }
        return 0
    }


    @CommandLine.Option(names = ["--public-key-file"], description = ["public key file"])
    lateinit var publicKeyFile: String

    @CommandLine.Option(names = ["--private-key-file"], description = ["private key file"])
    lateinit var privateKeyFile: String

    /**
     * needs [publicKeyFile] and [privateKeyFile]
     */
    private fun initKeys() {
        val crypter = Crypter()
        crypter.initKeys(publicKeyFile, privateKeyFile, password)
    }


    @CommandLine.Option(names = ["--zip-file-name"], description = ["zip file name"])
    lateinit var zipFileName: String

    @CommandLine.Option(names = ["--bundle-folder-name"], description = ["bundle folder name"])
    lateinit var bundleFolderPath: String

    /**
     * needs [publicKeyFile], [zipFileName] and [bundleFolderPath]
     */
    private fun encrypt() {
        val crypter = Crypter()
        val r = java.security.SecureRandom()
        val l = r.nextLong()
        val t = Instant.now().toEpochMilli()
        val token: String = "$l:$t"

        val encodedMessage = crypter.encrypt(token, "public.key")
        FileOutputStream("token.txt").use { fos -> fos.write(encodedMessage.toByteArray()) }
        // zip the files with passwd
        FileZipper(zipFileName, l.toString().toCharArray()).zipFolder(bundleFolderPath)
        // wrap the zip file and token.txt into another zip file.
        FileZipper("bd-$zipFileName").zipFiles("token.txt", zipFileName)

    }

    /**
     * needs [privateKeyFile], [zipFileName] and [target]
     */
    @CommandLine.Option(names = ["--target"], description = ["bundle folder name"])
    lateinit var target: String

    @CommandLine.Option(names = ["--password"], arity = "0..1", interactive = true)
    lateinit var password: CharArray
    private fun decrypt() {

        FileZipper("bd-$zipFileName").extractTo(target)
        val bytes = FileInputStream("$target/token.txt").use { fis -> fis.readBytes() }
        val crypter = Crypter()
        val token = crypter.decrypt(bytes.toString(StandardCharsets.UTF_8), privateKeyFile, password)
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
        FileZipper("$target/$zipFileName", l.toCharArray()).extractTo(target)
    }

    private fun help() {
        CommandLine.usage(this, System.out)
    }


}