package cn.galaxypokemon.playerareaevent.model.shape

import org.bukkit.Location

/**
 * 区域形状接口
 * 所有区域形状都需要实现这个接口
 */
interface AreaShape {
    
    /**
     * 检查位置是否在区域内
     */
    fun contains(location: Location): Boolean
    
    /**
     * 获取区域中心点
     */
    fun getCenter(): Location
    
    /**
     * 获取世界名称
     */
    fun getWorldName(): String
    
    /**
     * 获取边界框（用于优化检测）
     */
    fun getBoundingBox(): BoundingBox
}

/**
 * 边界框
 * 用于快速检测位置是否可能在区域内
 */
data class BoundingBox(
    val minX: Double,
    val minY: Double,
    val minZ: Double,
    val maxX: Double,
    val maxY: Double,
    val maxZ: Double
) {
    /**
     * 检查位置是否在边界框内
     */
    fun contains(x: Double, y: Double, z: Double): Boolean {
        return x in minX..maxX && y in minY..maxY && z in minZ..maxZ
    }
}

