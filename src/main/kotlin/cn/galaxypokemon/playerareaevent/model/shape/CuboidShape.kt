package cn.galaxypokemon.playerareaevent.model.shape

import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.math.max
import kotlin.math.min

/**
 * 立方体形状
 * 最常用的区域形状
 */
class CuboidShape(
    private val worldName: String,
    private val minX: Double,
    private val minY: Double,
    private val minZ: Double,
    private val maxX: Double,
    private val maxY: Double,
    private val maxZ: Double
) : AreaShape {

    private val boundingBox = BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)

    override fun contains(location: Location): Boolean {
        if (location.world?.name != worldName) return false
        return boundingBox.contains(location.x, location.y, location.z)
    }

    override fun getCenter(): Location {
        val world = Bukkit.getWorld(worldName) ?: throw IllegalStateException("World $worldName not found")
        return Location(
            world,
            (minX + maxX) / 2,
            (minY + maxY) / 2,
            (minZ + maxZ) / 2
        )
    }

    override fun getWorldName(): String = worldName

    override fun getBoundingBox(): BoundingBox = boundingBox

    companion object {
        /**
         * 从两个点创建立方体
         */
        fun fromPoints(worldName: String, pos1: Triple<Double, Double, Double>, pos2: Triple<Double, Double, Double>): CuboidShape {
            return CuboidShape(
                worldName,
                min(pos1.first, pos2.first),
                min(pos1.second, pos2.second),
                min(pos1.third, pos2.third),
                max(pos1.first, pos2.first),
                max(pos1.second, pos2.second),
                max(pos1.third, pos2.third)
            )
        }
    }

    override fun toString(): String {
        return "Cuboid($worldName: [$minX,$minY,$minZ] -> [$maxX,$maxY,$maxZ])"
    }
}

