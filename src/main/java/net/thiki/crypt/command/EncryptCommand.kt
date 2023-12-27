package net.thiki.crypt.command

import net.thiki.crypt.CommandLine
import net.thiki.crypt.Crypter
import net.thiki.crypt.FileZipper
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.time.Instant
import java.util.concurrent.Callable
import kotlin.io.path.pathString

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
        if (path.parent != null && path.parent.toFile().exists()){
            return path.parent.pathString + "/" + prefix + path.fileName
        }else{
            return prefix + path.fileName
        }
    }
}