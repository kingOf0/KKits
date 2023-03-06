package com.kingOf0.kits.util.base

import org.bukkit.inventory.ItemStack

class Replacer {

    val map = HashMap<String, String>()

    fun add(key: String, value: Any?) : Replacer {
        value?.let {
            map["%$key%"] = it.toString()
        }
        return this
    }

    fun replace(base: String) : String {
        var s = base
        for (entry in map) {
            s = s.replace(entry.key, entry.value)
        }
        return s
    }

    fun replace(base: List<String>) : List<String> {
        val result = ArrayList<String>()
        for (s in base) {
            result.add(replace(s))
        }
        return result
    }

    fun replace(vararg base: String) : List<String> {
        return replace(base.toList())
    }

}

fun ItemStack.replace(replacer: Replacer?): ItemStack {
    replacer?.let {
        this.itemMeta = this.itemMeta!!.apply {
            if (hasDisplayName()) {
                setDisplayName(replacer.replace(displayName))
            }
            if (hasLore()) {
                lore = replacer.replace(lore!!)
            }
        }
    }
    return this
}