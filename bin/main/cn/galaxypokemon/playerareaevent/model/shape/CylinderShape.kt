package cn.galaxypokemon.playerareaevent.model.shape

import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * 圆柱体形状
 */
class CylinderShape(
    private val worldName: String,
    private val centerX: Double,
    private val centerZ: Double,
    private val radius: Double,
    private val minY: Double,
    private val maxY: Double
) : AreaShape {

    private val radiusSquared = radius * radius
    private val boundingBox = BoundingBox(
        centerX - radius, minY, centerZ - radius,
        centerX + radius, maxY, centerZ + radius
    )

    override fun contains(location: Location): Boolean {
        if (location.world?.name != worldName) return false
        if (location.y !in minY..maxY) return false

        val dx = location.x - centerX
        val dz = location.z - centerZ
        return (dx * dx + dz * dz) <= radiusSquared
    }

    override fun getCenter(): Location {
        val world = Bukkit.getWorld(worldName) ?: throw IllegalStateException("World $worldName not found")
        return Location(world, centerX, (minY + maxY) / 2, centerZ)
    }

    override fun getWorldName(): String = worldName

    override fun getBoundingBox(): BoundingBox = boundingBox

    override fun toString(): String {
        return "Cylinder($worldName: center=[$centerX,$centerZ], radius=$radius, y=[$minY,$maxY])"
    }
}

