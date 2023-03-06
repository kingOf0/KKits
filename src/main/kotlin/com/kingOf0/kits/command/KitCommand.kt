package com.kingOf0.kits.command

import com.kingOf0.kits.KKits
import com.kingOf0.kits.PLUGIN_INSTANCE
import com.kingOf0.kits.SMART_INVENTORY
import com.kingOf0.kits.manager.FileManager
import com.kingOf0.kits.manager.KitManager
import com.kingOf0.kits.manager.SettingsManager
import com.kingOf0.kits.manager.SettingsManager.noPermission
import com.kingOf0.kits.manager.YamlItemManager
import com.kingOf0.kits.provider.ListProvider
import com.kingOf0.kits.provider.PreviewProvider
import com.kingOf0.kits.shade.smartinventory.Page
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KitCommand(command: String, aliases: List<String>) : AMyCommand<KKits>(PLUGIN_INSTANCE, command) {

    init {
        setAliases(aliases)
    }

    override fun onCommand(commandSender: CommandSender, p1: Command?, p2: String?, args: Array<String>): Boolean {
        when (args.size) {
            1 -> {
                if (args[0] == "reload") {
                    if (!commandSender.hasPermission("kkits.admin.reload")) {
                        commandSender.sendMessage(noPermission)
                        return true
                    }
                    FileManager.reload()
                    YamlItemManager.reload()
                    SettingsManager.reload()
                    KitManager.reload()
                    commandSender.sendMessage(SettingsManager.reloaded)
                    return true
                }
            }
            2, 3 -> {
                when (args[0]) {
                    "preview" -> {
                        if (commandSender !is Player) {
                            commandSender.sendMessage(SettingsManager.noConsole)
                            return true
                        }
                        val kit = KitManager.getKit(args[1])
                        if (kit == null) {
                            commandSender.sendMessage(SettingsManager.kitNotFound)
                            return true
                        }
                        val target = args.getOrNull(2)?.let {
                            Bukkit.getPlayer(it).also { player ->
                                if (player == null) {
                                    commandSender.sendMessage(SettingsManager.targetOffline)
                                    return true
                                }
                            }
                        } ?: commandSender
                        KitManager.openPreview(target, kit)
                        return true
                    }

                    "take" -> {
                        if (commandSender !is Player) {
                            commandSender.sendMessage(SettingsManager.noConsole)
                            return true
                        }
                        val kit = KitManager.getKit(args[1])
                        if (kit == null) {
                            commandSender.sendMessage(SettingsManager.kitNotFound)
                            return true
                        }
                        val target = args.getOrNull(2)?.let {
                            Bukkit.getPlayer(it).also { player ->
                                if (player == null) {
                                    commandSender.sendMessage(SettingsManager.targetOffline)
                                    return true
                                }
                            }
                        } ?: commandSender

                        KitManager.giveKit(target, kit)
                        return true
                    }
                }
            }
        }
        if (commandSender !is Player) {
            commandSender.sendMessage(SettingsManager.help)
            return true
        }
        Page.build(SMART_INVENTORY)
            .provider(ListProvider())
            .title(SettingsManager.listTitle)
            .row(SettingsManager.listRows)
            .open(commandSender)
        return true
    }
}