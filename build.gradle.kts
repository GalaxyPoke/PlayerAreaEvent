// ========================================
//     PlayerAreaEvent 构建配置文件
// ========================================

import io.izzel.taboolib.gradle.*

plugins {
    id("io.izzel.taboolib") version "2.0.27"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

// ========================================
// TabooLib 配置
// ========================================
taboolib {
    // 环境配置 - 安装需要的模块
    env {
        install(
            Bukkit,           // Bukkit 基础模块
            Kether,           // Kether 脚本引擎（执行区域动作脚本）
            Basic,            // 基础工具
            BukkitHook,       // Bukkit 钩子
            BukkitUtil,       // Bukkit 工具（颜色代码转换等）
            I18n,             // 国际化支持（多语言）
            MinecraftChat,    // 聊天组件（tell、title、actionbar）
            MinecraftEffect,
            CommandHelper
        )
    }

    // 版本配置
    version {
        taboolib = "6.2.4-3b3cd67"  // TabooLib 框架版本
    }
}

// ========================================
// 仓库配置（使用国内镜像加速）
// ========================================
repositories {
    mavenCentral()  // Maven 中央仓库（备用）

    // JitPack 仓库（用于 GitHub 项目）
    maven("https://jitpack.io")
}

// ========================================
// 依赖配置
// ========================================
dependencies {
    // Spigot/Paper 核心（1.19.2 版本）
    // compileOnly 表示编译时需要，但不打包进最终 jar
    compileOnly("ink.ptms.core:v11902:11902-minimize:mapped")
    compileOnly("ink.ptms.core:v11902:11902-minimize:universal")
    
    // Kotlin 标准库
    compileOnly(kotlin("stdlib"))
    
    // 本地 libs 文件夹中的额外依赖（如果有）
    compileOnly(fileTree("libs"))
}

// ========================================
// Java 编译配置
// ========================================
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"  // 使用 UTF-8 编码，避免中文乱码
}

// ========================================
// Kotlin 编译配置
// ========================================
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"  // 编译为 Java 8 字节码
        freeCompilerArgs = listOf("-Xjvm-default=all")  // 启用 JVM 默认方法
    }
}

// ========================================
// Java 版本配置
// ========================================
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8  // 源代码兼容 Java 8
    targetCompatibility = JavaVersion.VERSION_1_8  // 目标字节码为 Java 8
}

