package com.kingOf0.kits.util.item

import com.kingOf0.kits.manager.YamlItemManager
import com.kingOf0.kits.util.base.Replacer
import com.kingOf0.kits.util.base.replace
import org.bukkit.inventory.ItemStack

enum class KItem {
    INFO,
    COMMAND_PREVIEW,
    FILL,
    FILL_EMPTIES;

    fun set(contents: com.kingOf0.kits.shade.smartinventory.InventoryContents, function: (ItemStack) -> ItemStack = {it}): com.kingOf0.kits.shade.smartinventory.Icon? {
        return YamlItemManager.items[this]?.let { item ->
            com.kingOf0.kits.shade.smartinventory.Icon.from(function.invoke(item.itemStack.clone())).also { icon->
                contents.set(item.row, item.column, icon)
            }
        }
    }
    fun get(function: (ItemStack) -> ItemStack = {it}): ItemStack? {
        return YamlItemManager.items[this]?.let { item ->
            function.invoke(item.itemStack.clone())
        }
    }
    fun fill(contents: com.kingOf0.kits.shade.smartinventory.InventoryContents) {
        YamlItemManager.items[this]?.let { item ->
            com.kingOf0.kits.shade.smartinventory.Icon.from(item.itemStack.clone()).also { icon->
                contents.fill(icon)
            }
        }
    }

    fun fillEmpties(contents: com.kingOf0.kits.shade.smartinventory.InventoryContents) {
        YamlItemManager.items[this]?.let { item ->
            com.kingOf0.kits.shade.smartinventory.Icon.from(item.itemStack.clone()).also { icon->
                contents.fillEmpties(icon)
            }
        }
    }

    fun replace(replacer: Replacer): ItemStack? {
        return YamlItemManager.items[this]?.itemStack?.clone()?.replace(replacer)
    }
}