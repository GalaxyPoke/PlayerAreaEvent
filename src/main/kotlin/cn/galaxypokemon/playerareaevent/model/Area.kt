package cn.galaxypokemon.playerareaevent.model

import cn.galaxypokemon.playerareaevent.model.shape.AreaShape
import org.bukkit.Location

/**
 * 区域数据类
 * 代表一个完整的区域配置
 */
data class Area(
    val id: String,                          // 区域ID
    val shape: AreaShape,                    // 区域形状
    val priority: Int = 0,                   // 优先级（数字越大优先级越高）
    val actions: AreaActions = AreaActions(),// 区域动作
    val conditions: AreaConditions? = null,  // 进入/离开条件
    val flags: AreaFlags? = null,            // 区域标志
    val tickPeriod: Long = 20L               // Tick周期（单位：tick）
) {
    /**
     * 检查位置是否在区域内
     */
    fun contains(location: Location): Boolean {
        return shape.contains(location)
    }

    /**
     * 获取区域中心点
     */
    fun getCenter(): Location {
        return shape.getCenter()
    }

    /**
     * 获取区域世界名
     */
    fun getWorldName(): String {
        return shape.getWorldName()
    }

    override fun toString(): String {
        return "Area(id='$id', shape=${shape.javaClass.simpleName}, priority=$priority)"
    }
}

/**
 * 区域动作
 * 定义区域的各种触发动作
 */
data class AreaActions(
    val enter: String? = null,      // 进入动作
    val leave: String? = null,      // 离开动作
    val tick: String? = null,       // 持续动作
    val damage: String? = null,     // 受伤动作
    val kill: String? = null,       // 击杀动作
    val death: String? = null,      // 死亡动作
    val breakBlock: String? = null, // 破坏方块动作
    val placeBlock: String? = null, // 放置方块动作
    val interact: String? = null    // 交互动作
) {
    /**
     * 检查是否有任何动作
     */
    fun hasAnyAction(): Boolean {
        return enter != null || leave != null || tick != null ||
                damage != null || kill != null || death != null ||
                breakBlock != null || placeBlock != null || interact != null
    }
}

/**
 * 区域条件
 * 定义进入/离开区域的条件
 */
data class AreaConditions(
    val enterCheck: String? = null,     // 进入检查脚本
    val leaveCheck: String? = null,     // 离开检查脚本
    val denyActions: String? = null,    // 条件不满足时的动作
    val cancelOnDeny: Boolean = true    // 条件不满足时是否取消事件
)

/**
 * 区域标志
 * 控制区域内的各种行为
 */
data class AreaFlags(
    val pvp: Boolean? = null,           // PVP开关
    val godMode: Boolean? = null,       // 无敌模式
    val fly: Boolean? = null,           // 飞行
    val blockBreak: Boolean? = null,    // 破坏方块
    val blockPlace: Boolean? = null,    // 放置方块
    val mobSpawning: Boolean? = null,   // 生物生成
    val explosion: Boolean? = null,     // 爆炸
    val fireSpread: Boolean? = null,    // 火焰蔓延
    val hunger: Boolean? = null,        // 饥饿
    val itemDrop: Boolean? = null,      // 物品掉落
    val itemPickup: Boolean? = null,    // 物品拾取
    val mobDamage: Boolean? = null,     // 生物伤害
    val fallDamage: Boolean? = null,    // 摔落伤害
    val drowning: Boolean? = null,      // 溺水
    val burning: Boolean? = null,       // 燃烧
    val leafDecay: Boolean? = null,     // 树叶凋零
    val cropGrowth: Boolean? = null,    // 作物生长
    val weatherChange: Boolean? = null, // 天气变化
    val timeChange: Boolean? = null     // 时间变化
) {
    /**
     * 检查指定标志是否允许
     * @return true=允许, false=禁止, null=未设置
     */
    fun isAllowed(flag: String): Boolean? {
        return when (flag.lowercase()) {
            "pvp" -> pvp
            "god-mode", "godmode" -> godMode
            "fly" -> fly
            "block-break", "blockbreak" -> blockBreak
            "block-place", "blockplace" -> blockPlace
            "mob-spawning", "mobspawning" -> mobSpawning
            "explosion" -> explosion
            "fire-spread", "firespread" -> fireSpread
            "hunger" -> hunger
            "item-drop", "itemdrop" -> itemDrop
            "item-pickup", "itempickup" -> itemPickup
            "mob-damage", "mobdamage" -> mobDamage
            "fall-damage", "falldamage" -> fallDamage
            "drowning" -> drowning
            "burning" -> burning
            "leaf-decay", "leafdecay" -> leafDecay
            "crop-growth", "cropgrowth" -> cropGrowth
            "weather-change", "weatherchange" -> weatherChange
            "time-change", "timechange" -> timeChange
            else -> null
        }
    }
}

