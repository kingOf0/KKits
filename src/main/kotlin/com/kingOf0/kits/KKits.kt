package com.kingOf0.kits

import com.kingOf0.kits.manager.*
import com.kingOf0.kits.shade.smartinventory.SmartInventory
import com.kingOf0.kits.shade.smartinventory.manager.BasicSmartInventory
import com.kingOf0.kits.util.KUtils.disable
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

lateinit var PLUGIN_INSTANCE: KKits
lateinit var SMART_INVENTORY: SmartInventory
lateinit var LOGGER: Logger
lateinit var ECONOMY: Economy

class KKits : JavaPlugin() {

    override fun onLoad() {
        PLUGIN_INSTANCE = this
        SMART_INVENTORY = BasicSmartInventory(this)
        LOGGER = logger
    }

    override fun onEnable() {
        FileManager.initialize()
        SettingsManager.initialize()
        YamlItemManager.initialize()

        SMART_INVENTORY.init()

        if (!setupEconomy()) {
            disable("No Vault dependency found!")
            return
        }

        KitManager.initialize()
        DatabaseManager.initialize()
        RegisterManager.initialize()
    }

    //setupEconomy
    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        ECONOMY = rsp.provider
        return true
    }

}