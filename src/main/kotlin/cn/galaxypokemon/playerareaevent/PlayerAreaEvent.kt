package cn.galaxypokemon.playerareaevent

import cn.galaxypokemon.playerareaevent.core.AreaManager
import cn.galaxypokemon.playerareaevent.core.ConfigManager
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

/**
 * PlayerAreaEvent 主类
 * 一个优雅的区域管理插件
 */
object PlayerAreaEvent : Plugin() {

    override fun onEnable() {
        info("&a[PlayerAreaEvent] &f插件已启用")
        info("&a[PlayerAreaEvent] &f作者: GalaxyPokemon")
    }

    override fun onDisable() {
        info("&c[PlayerAreaEvent] &f插件已卸载")
    }

    /**
     * 插件初始化
     */
    @Awake(LifeCycle.ENABLE)
    fun init() {
        // 加载配置
        ConfigManager.load()
        // 加载区域
        AreaManager.load()

        info("&a[PlayerAreaEvent] &f已加载 ${AreaManager.getAreaCount()} 个区域")
    }

    /**
     * 自动重载配置
     */
    @Awake(LifeCycle.ACTIVE)
    fun setupAutoReload() {
        ConfigManager.setupAutoReload()
    }

    /**
     * 重载插件
     */
    fun reload() {
        ConfigManager.load()
        AreaManager.load()
        info("&a[PlayerAreaEvent] &f重载完成！")
    }
}

