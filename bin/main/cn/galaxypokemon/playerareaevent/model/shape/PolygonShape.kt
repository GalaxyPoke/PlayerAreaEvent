package cn.galaxypokemon.playerareaevent.model.shape

import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * 多边形形状（2D多边形 + Y轴范围）
 * 使用射线法判断点是否在多边形内
 */
class PolygonShape(
    private val worldName: String,
    private val points: List<Pair<Double, Double>>, // (x, z) 坐标列表
    private val minY: Double,
    private val maxY: Double
) : AreaShape {

    private val boundingBox: BoundingBox

    init {
        require(points.size >= 3) { "多边形至少需要3个点" }

        val minX = points.minOf { it.first }
        val maxX = points.maxOf { it.first }
        val minZ = points.minOf { it.second }
        val maxZ = points.maxOf { it.second }

        boundingBox = BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
    }

    override fun contains(location: Location): Boolean {
        if (location.world?.name != worldName) return false
        if (location.y !in minY..maxY) return false

        return isPointInPolygon(location.x, location.z)
    }

    /**
     * 射线法判断点是否在多边形内
     */
    private fun isPointInPolygon(x: Double, z: Double): Boolean {
        var inside = false
        var j = points.size - 1

        for (i in points.indices) {
            val xi = points[i].first
            val zi = points[i].second
            val xj = points[j].first
            val zj = points[j].second

            if ((zi > z) != (zj > z) && x < (xj - xi) * (z - zi) / (zj - zi) + xi) {
                inside = !inside
            }
            j = i
        }

        return inside
    }

    override fun getCenter(): Location {
        val world = Bukkit.getWorld(worldName) ?: throw IllegalStateException("World $worldName not found")
        
        // 计算多边形中心（质心）
        val centerX = points.map { it.first }.average()
        val centerZ = points.map { it.second }.average()
        
        return Location(world, centerX, (minY + maxY) / 2, centerZ)
    }

    override fun getWorldName(): String = worldName

    override fun getBoundingBox(): BoundingBox = boundingBox

    override fun toString(): String {
        return "Polygon($worldName: ${points.size} points, y=[$minY,$maxY])"
    }
}

