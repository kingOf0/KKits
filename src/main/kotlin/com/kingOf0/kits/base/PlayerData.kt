package com.kingOf0.kits.base

class PlayerData {

    private val kitUsageData: HashMap<String, Long>

    constructor() {
        this.kitUsageData = HashMap()
    }

    constructor(string: String) {
        val data = HashMap<String, Long>()
        string.split(",").forEach {
            val split = it.split(":")
            data[split[0]] = split[1].toLong()
        }
        this.kitUsageData = data
    }

    /**
     * Checks if the player is on cooldown for a kit.
     * @param kitName The name of the kit.
     * @param cooldown The cooldown of the kit. (in milliseconds)
     */
    fun isOnCooldown(kit: KitData): Boolean {
        val lastUsage = kitUsageData[kit.id] ?: return false
        return System.currentTimeMillis() < lastUsage + kit.cooldown
    }

    /**
     * Sets the last usage of a kit.
     * @param kitName The name of the kit.
     */
    fun setLastUsage(kit: KitData) {
        kitUsageData[kit.id] = System.currentTimeMillis()
    }

    fun serializeData(): String {
        return kitUsageData.entries.joinToString(separator = ",") { "${it.key}:${it.value}" }
    }


}
