package com.kingOf0.kits.manager

import com.kingOf0.kits.LOGGER
import com.kingOf0.kits.PLUGIN_INSTANCE
import com.kingOf0.kits.command.KitCommand
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

object RegisterManager : IManager("RegisterManager") {

    override fun load(): Boolean {
        LOGGER.info("RegisterManager is initializing...")
        runBlocking {
            LOGGER.info("RegisterManager | Register started")
            DatabaseManager.setup()
            LOGGER.info("RegisterManager | Database setup complete")

            register()
            LOGGER.info("RegisterManager | Register completed")
        }
        return true
    }

    private fun register() {
        //registering command
        object : BukkitRunnable() {
            override fun run() {
                //causes concurrent modify exception if not run (sync) in this runnable.
                KitCommand("kkits", SettingsManager.aliases).register()
            }
        }.runTask(PLUGIN_INSTANCE)

        //registering listeners.
        val pluginManager = Bukkit.getPluginManager()
//        if (registerSpawnListener) pluginManager.registerEvents(SpawnListener(), PLUGIN_INSTANCE)
    }


}