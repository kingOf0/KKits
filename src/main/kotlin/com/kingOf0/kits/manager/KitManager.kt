package com.kingOf0.kits.manager

import com.kingOf0.kits.base.KitData
import com.kingOf0.kits.PLUGIN_INSTANCE
import com.kingOf0.kits.SMART_INVENTORY
import com.kingOf0.kits.base.PlayerData
import com.kingOf0.kits.provider.PreviewProvider
import com.kingOf0.kits.shade.smartinventory.Page
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

object KitManager : IManager("KitManager") {

    private val kit = mutableMapOf<String, KitData>()
    private val cachedData = ConcurrentHashMap<UUID, PlayerData>()

    override fun load(): Boolean {
        FileManager.kits.apply {
            for (key in getKeys(false)) {
                val kitSection = getConfigurationSection(key) ?: continue
                val id = key.lowercase(Locale.ENGLISH)
                kit[id] = KitData(id, kitSection)
            }
        }
        return true
    }

    fun getPlayerData(uuid: UUID) = CompletableFuture<PlayerData>().apply {
        cachedData[uuid]?.let { playerData ->
            complete(playerData)
            return@apply
        }

        ExecutionManager.addQueue {
            val value = DatabaseManager.load(uuid) ?: PlayerData()
            cachedData.putIfAbsent(uuid, value)
            complete(value)
        }
    }

    fun savePlayerData(uuid: UUID, data: PlayerData) {
        ExecutionManager.addQueue {
            DatabaseManager.save(uuid, data)
        }
    }

    fun reload() {
        kit.clear()
        load()
    }

    fun deletePlayerData(uuid: UUID) {
        cachedData.remove(uuid)
    }

    fun getKit(id: String): KitData? {
        return kit[id.lowercase(Locale.ENGLISH)]
    }

    fun getKits(): Collection<KitData> {
        return kit.values
    }

    fun openPreview(player: Player, kit: KitData) {
        Page.build(SMART_INVENTORY)
            .provider(PreviewProvider(kit))
            .title(SettingsManager.previewTitle)
            .row(kit.rows)
            .open(player)
    }


    fun giveKit(player: Player, kit: KitData) {
        if (!player.hasPermission(kit.permission)) {
            player.sendMessage(SettingsManager.noPermission)
            return
        }
        getPlayerData(player.uniqueId).thenAccept { playerData ->
            if (playerData.isOnCooldown(kit)) {
                player.sendMessage(SettingsManager.onCooldown)
                return@thenAccept
            }
            Bukkit.getScheduler().runTask(PLUGIN_INSTANCE, Runnable {
                player.sendMessage(SettingsManager.givenKit.replace("%kit%", kit.name).replace("%player%", player.name))
                kit.giveItems(player)
                kit.executeCommands(player)
            })
            playerData.setLastUsage(kit)
            savePlayerData(player.uniqueId, playerData)
        }
    }

}