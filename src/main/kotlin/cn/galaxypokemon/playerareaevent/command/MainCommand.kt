package cn.galaxypokemon.playerareaevent.command

import cn.galaxypokemon.playerareaevent.PlayerAreaEvent
import cn.galaxypokemon.playerareaevent.core.AreaManager
import cn.galaxypokemon.playerareaevent.core.ConfigManager
import cn.galaxypokemon.playerareaevent.core.SelectionManager
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

/**
 * 主命令
 */
@CommandHeader(name = "playerareaevent", aliases = ["pa"], permission = "playerareaevent.command")
object MainCommand {

    /**
     * 发送带颜色的消息
     */
    private fun CommandSender.sendColoredMessage(message: String) {
        this.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            PlayerAreaEvent.reload()
            sender.sendColoredMessage("&a[PlayerAreaEvent] &f重载完成！")
        }
    }

    @CommandBody
    val list = subCommand {
        execute<CommandSender> { sender, _, _ ->
            val areas = AreaManager.getAllAreas()
            sender.sendColoredMessage("&a[PlayerAreaEvent] &f区域列表 (共 ${areas.size} 个):")
            areas.forEach { area ->
                sender.sendColoredMessage("  &e${area.id} &7- &f${area.shape.javaClass.simpleName} &7(优先级: ${area.priority})")
            }
        }
    }

    @CommandBody
    val info = subCommand {
        dynamic(comment = "区域ID") {
            suggestion<Player> { _, _ ->
                AreaManager.getAllAreas().map { it.id }
            }
            execute<CommandSender> { sender, _, argument ->
                val areaId = argument
                val area = AreaManager.getArea(areaId)

                if (area == null) {
                    sender.sendColoredMessage("&c[PlayerAreaEvent] &f区域不存在: $areaId")
                    return@execute
                }

                sender.sendColoredMessage("&a[PlayerAreaEvent] &f区域信息: &e$areaId")
                sender.sendColoredMessage("  &7形状: &f${area.shape.javaClass.simpleName}")
                sender.sendColoredMessage("  &7优先级: &f${area.priority}")
                // 修复：获取区域中心点的世界名称
                sender.sendColoredMessage("  &7世界: &f${area.getCenter().world?.name ?: "未知"}")
            }
        }
    }

    @CommandBody
    val tp = subCommand {
        dynamic(comment = "区域ID") {
            suggestion<Player> { _, _ ->
                AreaManager.getAllAreas().map { it.id }
            }
            execute<Player> { sender, _, argument ->
                val areaId = argument
                val area = AreaManager.getArea(areaId)

                if (area == null) {
                    sender.sendColoredMessage("&c[PlayerAreaEvent] &f区域不存在: $areaId")
                    return@execute
                }

                sender.teleport(area.getCenter())
                sender.sendColoredMessage("&a[PlayerAreaEvent] &f已传送到区域: $areaId")
            }
        }
    }

    @CommandBody
    val check = subCommand {
        execute<Player> { sender, _, _ ->
            val areas = AreaManager.getAreasAt(sender.location)
            if (areas.isEmpty()) {
                sender.sendColoredMessage("&c[PlayerAreaEvent] &f你当前不在任何区域内")
            } else {
                sender.sendColoredMessage("&a[PlayerAreaEvent] &f你当前在以下区域内:")
                areas.forEach { area ->
                    sender.sendColoredMessage("  &e${area.id} &7(优先级: ${area.priority})")
                }
            }
        }
    }

    @CommandBody
    val pos1 = subCommand {
        execute<Player> { sender, _, _ ->
            val location = sender.location
            SelectionManager.setPos1(sender, location)
            sender.sendColoredMessage("&a[PlayerAreaEvent] &f第一个点已设置: &e${location.blockX}, ${location.blockY}, ${location.blockZ}")
        }
    }

    @CommandBody
    val pos2 = subCommand {
        execute<Player> { sender, _, _ ->
            val location = sender.location
            SelectionManager.setPos2(sender, location)
            sender.sendColoredMessage("&a[PlayerAreaEvent] &f第二个点已设置: &e${location.blockX}, ${location.blockY}, ${location.blockZ}")
        }
    }

    @CommandBody
    val create = subCommand {
        dynamic(comment = "区域ID") {
            execute<Player> { sender, _, argument ->
                val areaId = argument

                // 检查区域ID是否已存在
                if (AreaManager.getArea(areaId) != null) {
                    sender.sendColoredMessage("&c[PlayerAreaEvent] &f区域已存在: $areaId")
                    return@execute
                }

                // 检查是否完成选点
                if (!SelectionManager.hasCompleteSelection(sender)) {
                    sender.sendColoredMessage("&c[PlayerAreaEvent] &f请先使用 /pa pos1 和 /pa pos2 选择区域范围")
                    return@execute
                }

                val selection = SelectionManager.getSelection(sender)!!
                val pos1 = selection.pos1!!
                val pos2 = selection.pos2!!

                // 检查两点是否在同一世界
                if (pos1.world != pos2.world) {
                    sender.sendColoredMessage("&c[PlayerAreaEvent] &f两个点必须在同一个世界")
                    return@execute
                }

                // 创建区域配置
                val areaConfig = createAreaConfig(areaId, pos1, pos2)

                // 保存到配置文件
                if (ConfigManager.saveNewArea(areaId, areaConfig)) {
                    // 重载配置以应用新区域
                    PlayerAreaEvent.reload()

                    sender.sendColoredMessage("&a[PlayerAreaEvent] &f区域创建成功: &e$areaId")
                    sender.sendColoredMessage("&7区域已自动重载并生效")

                    // 清除选点
                    SelectionManager.clearSelection(sender)
                } else {
                    sender.sendColoredMessage("&c[PlayerAreaEvent] &f保存区域配置失败")
                }
            }
        }
    }
    
    /**
     * 创建区域配置
     */
    private fun createAreaConfig(areaId: String, pos1: Location, pos2: Location): Map<String, Any> {
        return mapOf(
            "priority" to 10,
            "position" to mapOf(
                "shape" to "CUBOID",
                "world" to pos1.world!!.name,
                "position1" to "${pos1.blockX}, ${pos1.blockY}, ${pos1.blockZ}",
                "position2" to "${pos2.blockX}, ${pos2.blockY}, ${pos2.blockZ}"
            ),
            "actions" to mapOf(
                "enter" to "tell '&a欢迎进入区域: $areaId'"
            ),
            "flags" to mapOf<String, Any>()
        )
    }
}