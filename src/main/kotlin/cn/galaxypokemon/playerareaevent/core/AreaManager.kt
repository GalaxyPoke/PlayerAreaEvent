package cn.galaxypokemon.playerareaevent.core

import cn.galaxypokemon.playerareaevent.model.Area
import cn.galaxypokemon.playerareaevent.model.AreaActions
import cn.galaxypokemon.playerareaevent.model.AreaConditions
import cn.galaxypokemon.playerareaevent.model.AreaFlags
import cn.galaxypokemon.playerareaevent.model.shape.*
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap

/**
 * 区域管理器
 * 负责管理所有区域和玩家状态
 */
object AreaManager {

    // 所有区域 (区域ID -> 区域对象)
    private val areas = ConcurrentHashMap<String, Area>()

    // 玩家当前所在区域 (玩家名 -> 区域ID列表)
    private val playerAreas = ConcurrentHashMap<String, MutableSet<String>>()

    // 玩家区域Tick任务 (玩家名 -> (区域ID -> 任务))
    private val playerTickTasks = ConcurrentHashMap<String, MutableMap<String, PlatformExecutor.PlatformTask>>()

    /**
     * 加载所有区域
     */
    fun load() {
        areas.clear()

        val config = ConfigManager.areas
        val areasSection = config.getConfigurationSection("Areas") ?: run {
            info("&e[PlayerAreaEvent] &f未找到区域配置")
            return
        }

        for (areaId in areasSection.getKeys(false)) {
            try {
                val areaSection = areasSection.getConfigurationSection(areaId) ?: continue
                val area = parseArea(areaId, areaSection)
                areas[areaId] = area
            } catch (e: Exception) {
                info("&c[PlayerAreaEvent] &f加载区域 $areaId 失败: ${e.message}")
                e.printStackTrace()
            }
        }

        info("&a[PlayerAreaEvent] &f成功加载 ${areas.size} 个区域")
    }

    /**
     * 解析区域配置
     */
    private fun parseArea(id: String, section: ConfigurationSection): Area {
        // 解析形状
        val posSection = section.getConfigurationSection("position")
            ?: throw IllegalArgumentException("区域 $id 缺少 position 配置")
        
        val shape = parseShape(posSection)
        
        // 解析优先级
        val priority = section.getInt("priority", 0)
        
        // 解析动作
        val actionsSection = section.getConfigurationSection("actions")
        val actions = if (actionsSection != null) {
            AreaActions(
                enter = actionsSection.getString("enter"),
                leave = actionsSection.getString("leave"),
                tick = actionsSection.getString("tick"),
                damage = actionsSection.getString("damage"),
                kill = actionsSection.getString("kill"),
                death = actionsSection.getString("death"),
                breakBlock = actionsSection.getString("break-block"),
                placeBlock = actionsSection.getString("place-block"),
                interact = actionsSection.getString("interact")
            )
        } else {
            AreaActions()
        }
        
        // 解析条件
        val conditionsSection = section.getConfigurationSection("conditions")
        val conditions = if (conditionsSection != null) {
            AreaConditions(
                enterCheck = conditionsSection.getString("enter-check"),
                leaveCheck = conditionsSection.getString("leave-check"),
                denyActions = conditionsSection.getString("deny-actions"),
                cancelOnDeny = conditionsSection.getBoolean("cancel-on-deny", true)
            )
        } else null
        
        // 解析标志
        val flagsSection = section.getConfigurationSection("flags")
        val flags = if (flagsSection != null) {
            AreaFlags(
                pvp = flagsSection.get("pvp") as? Boolean,
                godMode = flagsSection.get("god-mode") as? Boolean,
                fly = flagsSection.get("fly") as? Boolean,
                blockBreak = flagsSection.get("block-break") as? Boolean,
                blockPlace = flagsSection.get("block-place") as? Boolean,
                mobSpawning = flagsSection.get("mob-spawning") as? Boolean,
                explosion = flagsSection.get("explosion") as? Boolean,
                fireSpread = flagsSection.get("fire-spread") as? Boolean,
                hunger = flagsSection.get("hunger") as? Boolean,
                itemDrop = flagsSection.get("item-drop") as? Boolean,
                itemPickup = flagsSection.get("item-pickup") as? Boolean,
                mobDamage = flagsSection.get("mob-damage") as? Boolean,
                fallDamage = flagsSection.get("fall-damage") as? Boolean,
                drowning = flagsSection.get("drowning") as? Boolean,
                burning = flagsSection.get("burning") as? Boolean,
                leafDecay = flagsSection.get("leaf-decay") as? Boolean,
                cropGrowth = flagsSection.get("crop-growth") as? Boolean,
                weatherChange = flagsSection.get("weather-change") as? Boolean,
                timeChange = flagsSection.get("time-change") as? Boolean
            )
        } else null
        
        // Tick周期
        val tickPeriod = section.getLong("tick-period", 20L)
        
        return Area(id, shape, priority, actions, conditions, flags, tickPeriod)
    }

    /**
     * 解析形状配置
     */
    private fun parseShape(section: ConfigurationSection): AreaShape {
        val shapeType = section.getString("shape")?.uppercase() ?: "CUBOID"
        val world = section.getString("world") ?: throw IllegalArgumentException("缺少 world 配置")

        return when (shapeType) {
            "CUBOID" -> {
                val position1 = section.getString("position1")?.split(",")?.map { it.trim().toDouble() }
                    ?: throw IllegalArgumentException("CUBOID 缺少 position1 配置")
                val position2 = section.getString("position2")?.split(",")?.map { it.trim().toDouble() }
                    ?: throw IllegalArgumentException("CUBOID 缺少 position2 配置")

                CuboidShape.fromPoints(
                    world,
                    Triple(position1[0], position1[1], position1[2]),
                    Triple(position2[0], position2[1], position2[2])
                )
            }

            "CIRCLE" -> {
                val center = section.getString("center")?.split(",")?.map { it.trim().toDouble() }
                    ?: throw IllegalArgumentException("CIRCLE 缺少 center 配置")
                val radius = section.getDouble("radius")
                val yMin = section.getDouble("y-min", 0.0)
                val yMax = section.getDouble("y-max", 256.0)

                CircleShape(world, center[0], center[2], radius, yMin, yMax)
            }

            "SPHERE" -> {
                val center = section.getString("center")?.split(",")?.map { it.trim().toDouble() }
                    ?: throw IllegalArgumentException("SPHERE 缺少 center 配置")
                val radius = section.getDouble("radius")

                SphereShape(world, center[0], center[1], center[2], radius)
            }

            "CYLINDER" -> {
                val center = section.getString("center")?.split(",")?.map { it.trim().toDouble() }
                    ?: throw IllegalArgumentException("CYLINDER 缺少 center 配置")
                val radius = section.getDouble("radius")
                val yMin = section.getDouble("y-min", 0.0)
                val yMax = section.getDouble("y-max", 256.0)

                CylinderShape(world, center[0], center[2], radius, yMin, yMax)
            }

            "POLYGON" -> {
                val pointsList = section.getStringList("points")
                val points = pointsList.map { pointStr ->
                    val coords = pointStr.split(",").map { it.trim().toDouble() }
                    Pair(coords[0], coords[1])
                }
                val yMin = section.getDouble("y-min", 0.0)
                val yMax = section.getDouble("y-max", 256.0)

                PolygonShape(world, points, yMin, yMax)
            }

            else -> throw IllegalArgumentException("未知的形状类型: $shapeType")
        }
    }

    /**
     * 获取区域数量
     */
    fun getAreaCount(): Int = areas.size

    /**
     * 获取指定位置的所有区域（按优先级排序）
     */
    fun getAreasAt(location: Location): List<Area> {
        return areas.values
            .filter { it.contains(location) }
            .sortedByDescending { it.priority }
    }

    /**
     * 获取玩家当前所在的区域
     */
    fun getPlayerAreas(player: Player): Set<String> {
        return playerAreas[player.name] ?: emptySet()
    }

    /**
     * 获取区域对象
     */
    fun getArea(id: String): Area? {
        return areas[id]
    }

    /**
     * 获取所有区域
     */
    fun getAllAreas(): Collection<Area> {
        return areas.values
    }

    /**
     * 检查玩家位置变化
     */
    fun checkPlayerMove(player: Player, to: Location, @Suppress("UNUSED_PARAMETER") from: Location) {
        val currentAreas = getAreasAt(to).map { it.id }.toSet()
        val previousAreas = playerAreas[player.name] ?: emptySet()

        // 进入的区域
        val enteredAreas = currentAreas - previousAreas
        // 离开的区域
        val leftAreas = previousAreas - currentAreas

        // 处理离开事件
        for (areaId in leftAreas) {
            val area = areas[areaId] ?: continue
            handleLeave(player, area)
        }

        // 处理进入事件
        for (areaId in enteredAreas) {
            val area = areas[areaId] ?: continue
            handleEnter(player, area)
        }

        // 更新玩家区域
        if (currentAreas.isEmpty()) {
            playerAreas.remove(player.name)
        } else {
            playerAreas[player.name] = currentAreas.toMutableSet()
        }
    }

    /**
     * 处理进入区域
     */
    private fun handleEnter(player: Player, area: Area) {
        // 执行进入动作
        area.actions.enter?.let { script ->
            ScriptExecutor.execute(player, script, area)
        }

        // 启动Tick任务
        if (area.actions.tick != null) {
            startTickTask(player, area)
        }
    }

    /**
     * 处理离开区域
     */
    private fun handleLeave(player: Player, area: Area) {
        // 停止Tick任务
        stopTickTask(player, area.id)

        // 执行离开动作
        area.actions.leave?.let { script ->
            ScriptExecutor.execute(player, script, area)
        }
    }

    /**
     * 启动Tick任务
     */
    private fun startTickTask(player: Player, area: Area) {
        val tasks = playerTickTasks.computeIfAbsent(player.name) { ConcurrentHashMap() }

        // 如果已有任务，先停止
        tasks[area.id]?.cancel()

        // 创建新任务
        val task = submit(period = area.tickPeriod, async = true) {
            if (!player.isOnline) {
                stopTickTask(player, area.id)
                return@submit
            }

            // 检查玩家是否还在区域内
            if (!area.contains(player.location)) {
                stopTickTask(player, area.id)
                return@submit
            }

            // 执行Tick动作
            area.actions.tick?.let { script ->
                ScriptExecutor.execute(player, script, area)
            }
        }

        tasks[area.id] = task
    }

    /**
     * 停止Tick任务
     */
    private fun stopTickTask(player: Player, areaId: String) {
        playerTickTasks[player.name]?.get(areaId)?.cancel()
        playerTickTasks[player.name]?.remove(areaId)
    }

    /**
     * 停止玩家所有Tick任务
     */
    fun stopAllTickTasks(player: Player) {
        playerTickTasks[player.name]?.values?.forEach { it.cancel() }
        playerTickTasks.remove(player.name)
        playerAreas.remove(player.name)
    }

    /**
     * 获取玩家所在区域的标志
     */
    fun getPlayerFlags(player: Player): AreaFlags? {
        val areas = getPlayerAreas(player)
            .mapNotNull { getArea(it) }
            .sortedByDescending { it.priority }

        return areas.firstOrNull()?.flags
    }
}

