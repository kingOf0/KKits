package com.kingOf0.kits.manager

import com.kingOf0.kits.LOGGER
import com.kingOf0.kits.base.PlayerData
import com.kingOf0.kits.database.IDatabase
import com.kingOf0.kits.database.MySqlDatabase
import com.kingOf0.kits.database.PostgreDatabase
import com.kingOf0.kits.database.SQLiteDatabase
import com.kingOf0.kits.manager.FileManager.config
import java.util.*

object DatabaseManager : IManager("DatabaseManager") {

    private lateinit var database: IDatabase

    public override fun load(): Boolean {
        val section = config.getConfigurationSection("database") ?: run {
            LOGGER.warning("Couldn't found 'database' section in config.yml")
            return false
        }
        database = when((section.getString("driver") ?: "").lowercase(Locale.ENGLISH)) {
            "postgre" -> {
                PostgreDatabase(section.getConfigurationSection("postgre") ?: run {
                    LOGGER.warning("Couldn't found 'postgre' section in config.yml")
                    return false
                })
            }
            "mysql" -> {
                MySqlDatabase(section.getConfigurationSection("mysql") ?: run {
                    LOGGER.warning("Couldn't found 'mysql' section in config.yml")
                    return false
                })
            }
            "sqlite" -> {
                SQLiteDatabase(section.getConfigurationSection("sqlite") ?: run {
                    LOGGER.warning("Couldn't found 'sqlite' section in config.yml")
                    return false
                })
            }
            else -> {
                LOGGER.warning("Couldn't found valid database driver in config.yml! Please type: [sqlite, postgre, mysql]")
                return false
            }
        }
        return true
    }

    internal suspend fun setup(): Boolean {
        database.setup()
        return database.test()
    }

    suspend fun save(uuid: UUID, data: PlayerData) {
        database.save(uuid, data)
    }

    suspend fun load(uuid: UUID): PlayerData? {
        return database.load(uuid)
    }

}