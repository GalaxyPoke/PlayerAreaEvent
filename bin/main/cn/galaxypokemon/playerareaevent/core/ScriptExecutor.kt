package cn.galaxypokemon.playerareaevent.core

import cn.galaxypokemon.playerareaevent.model.Area
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.module.kether.printKetherErrorMessage
import java.util.concurrent.CompletableFuture

/**
 * 脚本执行器
 * 负责执行 Kether 脚本
 */
object ScriptExecutor {

    /**
     * 将 & 颜色代码转换为 § 颜色代码
     */
    private fun translateColorCodes(text: String): String {
        return ChatColor.translateAlternateColorCodes('&', text)
    }

    /**
     * 执行脚本
     */
    fun execute(player: Player, script: String, area: Area): CompletableFuture<Any?> {
        return try {
            // 转换颜色代码
            val translatedScript = translateColorCodes(script)

            KetherShell.eval(
                translatedScript,
                ScriptOptions.new {
                    sender(player)
                    // 注入变量
                    set("player", player)
                    set("area_id", area.id)
                    set("area_priority", area.priority)
                    set("world", player.world)
                    set("location", player.location)
                }
            ).thenApply { result ->
                result
            }.exceptionally { error ->
                error.printKetherErrorMessage()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CompletableFuture.completedFuture(null)
        }
    }

    /**
     * 执行脚本并检查结果
     * @return true 表示通过检查，false 表示未通过
     */
    fun executeAndCheck(player: Player, script: String, area: Area): CompletableFuture<Boolean> {
        return execute(player, script, area).thenApply { result ->
            when (result) {
                is Boolean -> result
                is String -> result.equals("true", ignoreCase = true)
                is Number -> result.toInt() != 0
                null -> false
                else -> true
            }
        }
    }
}

