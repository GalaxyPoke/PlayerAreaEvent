package cn.galaxypokemon.playerareaevent.core

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

/**
 * 选点管理器
 * 管理玩家的区域选点
 */
object SelectionManager {
    
    // 玩家选点数据
    private val playerSelections = ConcurrentHashMap<String, PlayerSelection>()
    
    /**
     * 设置玩家的第一个点
     */
    fun setPos1(player: Player, location: Location) {
        val selection = getOrCreateSelection(player)
        selection.pos1 = location.clone()
    }
    
    /**
     * 设置玩家的第二个点
     */
    fun setPos2(player: Player, location: Location) {
        val selection = getOrCreateSelection(player)
        selection.pos2 = location.clone()
    }
    
    /**
     * 获取玩家的选点数据
     */
    fun getSelection(player: Player): PlayerSelection? {
        return playerSelections[player.name]
    }
    
    /**
     * 检查玩家是否已完成选点
     */
    fun hasCompleteSelection(player: Player): Boolean {
        val selection = getSelection(player)
        return selection != null && selection.pos1 != null && selection.pos2 != null
    }
    
    /**
     * 清除玩家的选点
     */
    fun clearSelection(player: Player) {
        playerSelections.remove(player.name)
    }
    
    /**
     * 获取或创建玩家选点数据
     */
    private fun getOrCreateSelection(player: Player): PlayerSelection {
        return playerSelections.computeIfAbsent(player.name) { PlayerSelection() }
    }
    
    /**
     * 玩家选点数据类
     */
    data class PlayerSelection(
        var pos1: Location? = null,
        var pos2: Location? = null
    )
}