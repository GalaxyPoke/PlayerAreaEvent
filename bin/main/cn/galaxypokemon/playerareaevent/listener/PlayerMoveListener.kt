package cn.galaxypokemon.playerareaevent.listener

import cn.galaxypokemon.playerareaevent.core.AreaManager
import org.bukkit.event.player.*
import taboolib.common.platform.event.SubscribeEvent

/**
 * 玩家移动监听器
 * 监听玩家移动并检测区域进入/离开
 */
object PlayerMoveListener {

    @SubscribeEvent
    fun onPlayerMove(event: PlayerMoveEvent) {
        val to = event.to ?: return
        val from = event.from
        
        // 只在方块位置改变时检测
        if (to.blockX == from.blockX && to.blockY == from.blockY && to.blockZ == from.blockZ) {
            return
        }

        AreaManager.checkPlayerMove(event.player, to, from)
    }

    @SubscribeEvent
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val to = event.to ?: return
        val from = event.from

        AreaManager.checkPlayerMove(event.player, to, from)
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val location = event.player.location
        AreaManager.checkPlayerMove(event.player, location, location)
    }

    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        AreaManager.stopAllTickTasks(event.player)
    }

    @SubscribeEvent
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        // 清除旧世界的区域状态
        AreaManager.stopAllTickTasks(event.player)
        
        // 检测新世界的区域
        val location = event.player.location
        AreaManager.checkPlayerMove(event.player, location, location)
    }
}

