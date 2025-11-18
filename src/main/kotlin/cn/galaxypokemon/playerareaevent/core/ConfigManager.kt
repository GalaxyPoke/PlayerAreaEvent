package cn.galaxypokemon.playerareaevent.core

import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

/**
 * 配置管理器
 * 负责管理所有配置文件
 */
object ConfigManager {

    @Config("config.yml", autoReload = true)
    lateinit var config: Configuration
        private set

    @Config("areas.yml", autoReload = true)
    lateinit var areas: Configuration
        private set

    @Config("lang/zh_CN.yml", autoReload = true)
    lateinit var lang: Configuration
        private set

    /**
     * 加载配置
     */
    fun load() {
        info("&a[ConfigManager] &f正在加载配置文件...")
        // TabooLib 会自动加载配置
    }

    /**
     * 设置自动重载
     */
    fun setupAutoReload() {
        config.onReload {
            info("&e[ConfigManager] &fconfig.yml 已重载")
        }

        areas.onReload {
            info("&e[ConfigManager] &fareas.yml 已重载")
            AreaManager.load()
        }

        lang.onReload {
            info("&e[ConfigManager] &flang/zh_CN.yml 已重载")
        }
    }

    /**
     * 获取配置项
     */
    fun getString(path: String, default: String = ""): String {
        return config.getString(path) ?: default
    }

    fun getInt(path: String, default: Int = 0): Int {
        return config.getInt(path, default)
    }

    fun getBoolean(path: String, default: Boolean = false): Boolean {
        return config.getBoolean(path, default)
    }

    fun getLong(path: String, default: Long = 0L): Long {
        return config.getLong(path, default)
    }


    /**
     * 保存新区域到配置文件
     */
    fun saveNewArea(areaId: String, areaConfig: Map<String, Any>): Boolean {
        return try {
            // 获取当前areas配置
            val areasSection = areas.getConfigurationSection("Areas") ?: areas.createSection("Areas")
            
            // 添加新区域
            val areaSection = areasSection.createSection(areaId)
            areaConfig.forEach { (key, value) ->
                areaSection.set(key, value)
            }
            
            // 保存配置文件
            areas.saveToFile()
            true
        } catch (e: Exception) {
            info("&c[ConfigManager] &f保存区域配置失败: ${e.message}")
            false
        }
    }
}