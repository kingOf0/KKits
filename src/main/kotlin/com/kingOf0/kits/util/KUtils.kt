package com.kingOf0.kits.util

import com.kingOf0.kits.PLUGIN_INSTANCE
import com.kingOf0.kits.manager.SettingsManager
import org.bukkit.Bukkit

object KUtils {


    private const val DAY = 86400000L
    private const val HOUR = 3600000L
    private const val MINUTE = 60000L
    private const val SECOND = 1000L


    fun disable(msg: String, throwable: Throwable) {
        throwable.printStackTrace()
        disable(msg)
    }

    fun disable(msg: String = "Plugin disabled!") {
        val stateException = IllegalStateException(
            "\n" +
                    "\t-----------------------\n" +
                    "\t-----------------------\n" +
                    "\t-----------------------\n" +
                    "\t-----------------------\n" +
                    "\t\t$msg...\n" +
                    "\t-----------------------\n" +
                    "\t-----------------------\n" +
                    "\t-----------------------\n"
        ).apply {
            stackTrace = stackTrace.take(5).toTypedArray()
        }
        stateException.printStackTrace()
        Bukkit.getPluginManager().disablePlugin(PLUGIN_INSTANCE)
        throw IllegalStateException(msg).apply {
            stackTrace = stackTrace.take(3).toTypedArray()
        }
    }


    fun formatMillis(time: Long) : String {
        var millis = time
        val days = millis / DAY
        millis -= days * DAY

        val hours = millis / HOUR
        millis -= hours * HOUR

        val minutes = millis / MINUTE
        millis -= minutes * MINUTE

        val seconds = millis / SECOND
        millis -= seconds * SECOND

        val builder = StringBuilder()

        if (days > 0) {
            builder.append("$days ${SettingsManager.days}")
        }
        if (hours > 0) {
            builder.append(" $hours ${SettingsManager.hours}")
        }
        if (minutes > 0) {
            builder.append(" $minutes ${SettingsManager.minutes}")
        }
        if (seconds > 0) {
            builder.append(" $seconds ${SettingsManager.seconds}")
        }
        if (builder.isEmpty()) builder.append("§a${SettingsManager.now}")
        return builder.toString()
    }

}