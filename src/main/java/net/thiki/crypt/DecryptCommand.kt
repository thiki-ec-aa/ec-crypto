package net.thiki.crypt

import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.Callable
import kotlin.io.path.pathString

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