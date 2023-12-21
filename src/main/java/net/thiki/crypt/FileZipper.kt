package net.thiki.crypt

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.EncryptionMethod
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


class FileZipper(
    zipFileName: String,
    passwd: CharArray? = null,
) {

    private val zipFile = ZipFile(zipFileName)
    private val zipParameters = ZipParameters().also{
        it.isEncryptFiles = passwd != null && passwd.isNotEmpty()
        it.encryptionMethod = EncryptionMethod.AES
    }

    init {
        logger.info("Zip file name: $zipFileName")
        if (passwd != null && passwd.isNotEmpty()) {
            zipFile.setPassword(passwd)
        }
    }


    init {

    }
    fun zipFolder(sourceFolder: String) {
        zipFile.use {
            it.addFolder(File(sourceFolder), zipParameters)
        }
        terminate()
    }

    /**
     * prevents the password extracted from the core-dump file
     */
    fun terminate() {
        zipFile.setPassword(null)
    }

    /**
     * zip files
     */
    fun zipFiles(vararg sourceFiles: String){
        zipFile.use {
            sourceFiles.forEach { file ->
                zipFile.addFile(File(file), zipParameters)
            }
        }
        terminate()
    }

    fun extractFile(file: String, target: String) {
        zipFile.use {
            it.extractFile(file, target)
        }
        terminate()
    }
    fun extractTo(target: String) {
        zipFile.use {
            it.extractAll(target)
        }
        terminate()
    }
    companion object{
        private val logger: Logger = LoggerFactory.getLogger(FileZipper::class.java)
    }

}