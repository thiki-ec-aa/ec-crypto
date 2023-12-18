package net.thiki.crypt

import java.io.FileOutputStream
import java.time.Instant
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
    var command: String = ""

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
    var publicKeyFile: String = ""

    @CommandLine.Option(names = ["--private-key-file"], description = ["private key file"])
    var privateKeyFile: String = ""

    /**
     * needs [publicKeyFile] and [privateKeyFile]
     */
    private fun initKeys() {
        val crypter = Crypter()
        crypter.initKeys(publicKeyFile, privateKeyFile)
    }


    @CommandLine.Option(names = ["--zip-file-name"], description = ["zip file name"])
    var zipFileName: String = ""

    @CommandLine.Option(names = ["--bundle-folder-name"], description = ["bundle folder name"])
    var bundleFolderPath: String = ""

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

    private fun decrypt() {
        TODO("Not yet implemented")
    }

    private fun help() {
        CommandLine.usage(BundlesToolApp(), System.out)
    }


}