package com.kingOf0.kits.manager

import com.kingOf0.kits.LOGGER
import com.kingOf0.kits.PLUGIN_INSTANCE
import com.kingOf0.kits.manager.EncryptManager.binarySecret
import com.kingOf0.kits.manager.EncryptManager.decode
import com.kingOf0.kits.manager.EncryptManager.encode
import com.kingOf0.kits.manager.EncryptManager.fromBinary
import com.kingOf0.kits.manager.EncryptManager.toBinary
import com.kingOf0.kits.util.base.LicenseResponse
import com.kingOf0.kits.util.base.State
import com.kingOf0.kits.util.config.ConfigFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

val LOGGER_TEMPLATE: String = "[%s}] [${PLUGIN_INSTANCE.name}]: %s\n"

object FileManager : IManager("FileManager") {

    private val dateFormat = SimpleDateFormat("hh:mm:ss")
    lateinit var config: ConfigFile
    lateinit var kits: ConfigFile
    lateinit var privateLogger: FileWriter

    override fun load(): Boolean {
        config = ConfigFile("config", PLUGIN_INSTANCE)
        kits = ConfigFile("kits", PLUGIN_INSTANCE)

        File(PLUGIN_INSTANCE.dataFolder, "log.txt").apply {
            if (!exists()) createNewFile()
            privateLogger = FileWriter(this, true)
        }
        val externalLicense = File("KLM.txt").takeIf { it.exists() }?.readLines()?.firstOrNull()?.takeIf { it.isNotBlank() }

        val license = ConfigFile("license", PLUGIN_INSTANCE).getString("license", "KEY_HERE")!!
        if (license.equals("KEY_HERE", ignoreCase = true) && externalLicense == null) {
            failMessage = "PLEASE ENTER LICENSE"
            return false
        }

        LOGGER.info("* LicenseManager | Load State: Creating license request...")
        val binaryLicense = toBinary(license)
        val binaryExternalLicense = externalLicense?.let { toBinary(it) }
        val binaryRandom = toBinary(UUID.randomUUID().toString())
        val binaryPluginName = toBinary(PLUGIN_INSTANCE.name)
        val encryptedSecret = encode(binarySecret, binaryRandom)
        val encryptedLicense = encode(binaryLicense, binaryRandom)
        val encryptedExternalLicense = binaryExternalLicense?.let { encode(it, binaryRandom) }
        val encryptedPluginName = encode(binaryPluginName, binaryRandom)


        LOGGER.info("* LicenseManager | Load State: Sending the request...")
        val connection = URL("https://rm5xx52zbd.execute-api.us-east-1.amazonaws.com/klms?secret=$encryptedSecret&license=$encryptedLicense&externalLicense=${encryptedExternalLicense}&pluginName=$encryptedPluginName").openConnection() as HttpURLConnection
        LOGGER.info("* LicenseManager | Load State: Reading response...")

        val readText = runCatching { InputStreamReader(connection.inputStream).readText() }.getOrNull()
        if (readText == null) {
            failMessage = "Error while decoding stream null"
            return false
        }

        val response: LicenseResponse? = runCatching { Json.decodeFromString<LicenseResponse>(readText) }.getOrNull()
        if (response == null) {
            failMessage = "Error while decoding license $readText"
            return false
        }

        val responseBinaryRandom = decode(response.secret, binarySecret)
        return if (State.valueOf(decode(response.result, responseBinaryRandom)) == State.SUCCESS) {
            println("License is valid")
            LOGGER.info("* LicenseManager | Load State: Ok.")
            LOGGER.info("+ LicenseManager | Successfully loaded.").also {
                SettingsManager.loadLateSupport = false
            }
            true
        } else {
            failMessage = decode(response.message, responseBinaryRandom)
            false
        }
    }

    fun log(obj: Any) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                privateLogger.write(LOGGER_TEMPLATE.format(dateFormat.format(System.currentTimeMillis()), obj.toString()))
                privateLogger.flush()
            }
        }.invokeOnCompletion {
            it?.printStackTrace()
        }
    }

    fun reload() {
        config.load()
        kits.load()
    }


}
