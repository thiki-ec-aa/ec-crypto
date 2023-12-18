package net.thiki.crypt

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("This test is for PoC only.")
class PoCTest{

    @Test
    fun testZipCommand(){
        val processBuilder = ProcessBuilder()
        val p = "hello kitty"
        // 7z a -r -tzip -Paaa hxlc-cone$(date +%Y-%m-%d).zip * -xr\!.git -x\!ar.sh
        processBuilder.command("c:\\Program Files\\7-Zip\\7z", "a", "-r", "-tzip",  "-P$p", "bundles.zip", "src/resource/bundles")
//        processBuilder.command("zip","-r", "-P '$p'", "bundles.zip", "src/resource/bundles")

        try {

            val process: Process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val line = reader.readText();
            println(line)

            val exitVal = process.waitFor()
            if (exitVal == 0) {
                println("Success!")
                //exitProcess(0)
            } else {
                //else...
                println("Failed!")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }


}