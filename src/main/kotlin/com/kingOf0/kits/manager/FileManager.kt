package com.kingOf0.kits.manager

import com.kingOf0.kits.PLUGIN_INSTANCE
import com.kingOf0.kits.util.config.ConfigFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat

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
        return true
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
