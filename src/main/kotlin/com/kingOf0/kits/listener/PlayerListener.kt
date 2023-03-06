package com.kingOf0.kits.listener

import com.kingOf0.kits.manager.KitManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener : Listener {

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        KitManager.deletePlayerData(event.player.uniqueId)
    }

}