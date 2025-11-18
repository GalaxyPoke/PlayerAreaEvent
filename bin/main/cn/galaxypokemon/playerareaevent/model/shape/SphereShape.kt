package cn.galaxypokemon.playerareaevent.model.shape

import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * 球体形状（3D球体）
 */
class SphereShape(
    private val worldName: String,
    private val centerX: Double,
    private val centerY: Double,
    private val centerZ: Double,
    private val radius: Double
) : AreaShape {

    private val radiusSquared = radius * radius
    private val boundingBox = BoundingBox(
        centerX - radius, centerY - radius, centerZ - radius,
        centerX + radius, centerY + radius, centerZ + radius
    )

    override fun contains(location: Location): Boolean {
        if (location.world?.name != worldName) return false

        val dx = location.x - centerX
        val dy = location.y - centerY
        val dz = location.z - centerZ
        return (dx * dx + dy * dy + dz * dz) <= radiusSquared
    }

    override fun getCenter(): Location {
        val world = Bukkit.getWorld(worldName) ?: throw IllegalStateException("World $worldName not found")
        return Location(world, centerX, centerY, centerZ)
    }

    override fun getWorldName(): String = worldName

    override fun getBoundingBox(): BoundingBox = boundingBox

    override fun toString(): String {
        return "Sphere($worldName: center=[$centerX,$centerY,$centerZ], radius=$radius)"
    }
}

