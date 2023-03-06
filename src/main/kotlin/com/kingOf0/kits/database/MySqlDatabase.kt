package com.kingOf0.kits.database

import com.kingOf0.kits.LOGGER
import org.bukkit.configuration.ConfigurationSection
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level

class MySqlDatabase(section: ConfigurationSection) :
    BaseDatabase("MySql") {

    private val url: String

    init {
        val host = section.getString("host", "localhost")!!
        val port = section.getInt("port", 3306)
        val database = section.getString("database", "minecraft")!!
        val username: String = section.getString("username", "root")!!
        val password: String = section.getString("password", "root")!!
        url = "jdbc:mysql://$host:$port/$database?user=$username&password=$password"
    }

    override suspend fun register() {
        Class.forName("com.mysql.jdbc.Driver")
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