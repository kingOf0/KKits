package com.kingOf0.kits.provider

import com.kingOf0.kits.manager.KitManager
import com.kingOf0.kits.manager.SettingsManager
import com.kingOf0.kits.shade.smartinventory.Icon
import com.kingOf0.kits.shade.smartinventory.InventoryContents
import com.kingOf0.kits.util.item.KInventoryProvider
import org.bukkit.event.inventory.ClickType

class ListProvider : KInventoryProvider("list") {

    override fun init(contents: InventoryContents) {
        setup(contents)
        val player = contents.player()

        for (kit in KitManager.getKits()) {
            kit.icon?.let {
                contents.add(Icon.cancel(it).whenClick { event ->
                    when(event.click()) {
                        SettingsManager.giveClick -> KitManager.giveKit(player, kit)
                        else -> KitManager.openPreview(player, kit)
                    }
                })
            }
        }
    }

}
