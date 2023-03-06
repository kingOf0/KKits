package com.kingOf0.kits.database

import com.kingOf0.kits.base.PlayerData
import java.sql.Connection
import java.util.*

interface IDatabase {

    suspend fun setup()
    suspend fun register()
    suspend fun initialize()
    suspend fun test(): Boolean

    suspend fun getConnection(): Connection?

    suspend fun close(closeable: AutoCloseable?)

    suspend fun save(uuid: UUID, data: PlayerData)
    suspend fun load(uuid: UUID): PlayerData?

    suspend fun setConfig(key: String, data: Any?)
    suspend fun loadConfig(key: String): String?
}