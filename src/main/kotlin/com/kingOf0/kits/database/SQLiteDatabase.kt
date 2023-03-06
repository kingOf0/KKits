package com.kingOf0.kits.database

import com.kingOf0.kits.LOGGER
import com.kingOf0.kits.PLUGIN_INSTANCE
import com.kingOf0.kits.util.KUtils.disable
import org.bukkit.configuration.ConfigurationSection
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level

class SQLiteDatabase(section: ConfigurationSection) :
    BaseDatabase("SQLite") {

    private val url: String

    init {
        val fileName = section.getString("file", "data") + ".db"
        val file = File(PLUGIN_INSTANCE.dataFolder, fileName)
        runCatching {
            file.createNewFile()
        }.onFailure {
            disable("Couldn't create file for sqlite database: $it")
        }
        url = "jdbc:sqlite:$file"
    }

    override suspend fun register() {
        Class.forName("org.sqlite.JDBC")
    }

    override suspend fun getConnection(): Connection? {
        try {
            return DriverManager.getConnection(url)
        } catch (e: SQLException) {
            LOGGER.log(Level.SEVERE, "SQL exception on initialize", e)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}