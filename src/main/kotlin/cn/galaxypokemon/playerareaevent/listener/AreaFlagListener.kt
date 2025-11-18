@file:Suppress("DEPRECATION")

package cn.galaxypokemon.playerareaevent.listener

import cn.galaxypokemon.playerareaevent.core.AreaManager
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * 区域标志监听器
 * 根据区域标志控制玩家行为
 */
object AreaFlagListener {

   @SubscribeEvent
    fun onPvP(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? Player ?: return
        event.damager as? Player ?: return  // 确保攻击者是玩家

        val flags = AreaManager.getPlayerFlags(victim) ?: return
        
        // 检查PVP标志
        if (flags.pvp == false) {
            event.isCancelled = true
        }
    }

    @SubscribeEvent
    fun onDamage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        val flags = AreaManager.getPlayerFlags(player) ?: return

        // 无敌模式
        if (flags.godMode == true) {
            event.isCancelled = true
            return
        }

        // 摔落伤害
        if (flags.fallDamage == false && event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
        }

        // 溺水
        if (flags.drowning == false && event.cause == EntityDamageEvent.DamageCause.DROWNING) {
            event.isCancelled = true
        }

        // 燃烧
        if (flags.burning == false && 
            (event.cause == EntityDamageEvent.DamageCause.FIRE || 
             event.cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
             event.cause == EntityDamageEvent.DamageCause.LAVA)) {
            event.isCancelled = true
        }

        // 生物伤害
        if (flags.mobDamage == false && event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            event.isCancelled = true
        }
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockBreakEvent) {
        val flags = AreaManager.getPlayerFlags(event.player) ?: return
        
        if (flags.blockBreak == false) {
            event.isCancelled = true
        }
    }

    @SubscribeEvent
    fun onBlockPlace(event: BlockPlaceEvent) {
        val flags = AreaManager.getPlayerFlags(event.player) ?: return
        
        if (flags.blockPlace == false) {
            event.isCancelled = true
        }
    }

    @SubscribeEvent
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return
        val flags = AreaManager.getPlayerFlags(player) ?: return
        
        if (flags.hunger == false) {
            event.isCancelled = true
        }
    }

    @SubscribeEvent  
    fun onItemDrop(event: PlayerDropItemEvent) {
        val flags = AreaManager.getPlayerFlags(event.player) ?: return
        
        if (flags.itemDrop == false) {
            event.isCancelled = true
        }
    }

    @Suppress("DEPRECATION")
    @SubscribeEvent
    fun onItemPickup(event: PlayerPickupItemEvent) {
        val flags = AreaManager.getPlayerFlags(event.player) ?: return
        
        if (flags.itemPickup == false) {
            event.isCancelled = true
        }
    }
}