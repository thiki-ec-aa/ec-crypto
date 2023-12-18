package net.thiki.crypt

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.EncryptionMethod
import java.io.File


class FileZipper {

    private val zipParameters = ZipParameters().apply {
        isEncryptFiles = true
        encryptionMethod = EncryptionMethod.AES
    }

    fun zipFolder(zipFileName: String, sourceFolder: String, passwd: CharArray){
        val zipFile = ZipFile(zipFileName, passwd)
        zipFile.use {
            it.addFolder(File(sourceFolder), zipParameters)
        }
        passwd.fill('0')
    }

    fun extract(zipFileName: String, target: String, passwd: CharArray) {
        val zipFile = ZipFile(zipFileName, passwd)
        zipFile.use {
            it.extractAll(target)
        }
        passwd.fill('0')
    }
}