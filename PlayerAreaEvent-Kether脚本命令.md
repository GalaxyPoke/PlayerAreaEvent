# PlayerArea - Kether 脚本命令大全

## 📖 说明

PlayerArea 使用 TabooLib 的 Kether 脚本引擎来执行区域动作。
由于已经安装了 `MinecraftEffect` 模块，你可以使用音效和粒子效果！

---

## 🎵 音效命令

### 基础语法
```yaml
sound <音效类型> [音量] [音调]
```

### 参数说明
- **音效类型** - Minecraft 音效名称（必填）
- **音量** - 音量大小，默认 1.0（可选）
- **音调** - 音调高低，默认 1.0（可选）

### 使用示例

#### 1. 播放简单音效
```yaml
actions:
  enter: |-
    sound ENTITY_PLAYER_LEVELUP
```

#### 2. 自定义音量和音调
```yaml
actions:
  enter: |-
    sound BLOCK_NOTE_BLOCK_PLING 1.0 2.0
```

#### 3. 多个音效组合
```yaml
actions:
  enter: |-
    sound ENTITY_ENDER_DRAGON_GROWL 1.0 1.0
    sound ENTITY_LIGHTNING_BOLT_THUNDER 0.5 1.5
```

### 常用音效列表

| 音效名称 | 说明 | 适用场景 |
|---------|------|---------|
| `ENTITY_PLAYER_LEVELUP` | 升级音效 | 进入特殊区域 |
| `BLOCK_NOTE_BLOCK_PLING` | 叮咚声 | 提示音 |
| `ENTITY_ENDER_DRAGON_GROWL` | 末影龙咆哮 | 危险区域 |
| `ENTITY_LIGHTNING_BOLT_THUNDER` | 雷声 | 战斗区域 |
| `BLOCK_PORTAL_AMBIENT` | 传送门环境音 | 传送区域 |
| `ENTITY_PLAYER_HURT` | 受伤音效 | 伤害反馈 |
| `ENTITY_EXPERIENCE_ORB_PICKUP` | 经验球拾取 | 奖励提示 |
| `BLOCK_ANVIL_LAND` | 铁砧落地 | 重要提示 |
| `ENTITY_VILLAGER_YES` | 村民同意 | 成功提示 |
| `ENTITY_VILLAGER_NO` | 村民拒绝 | 失败提示 |

---

## ✨ 粒子效果命令

### 基础语法
```yaml
particle <粒子类型> [数量] [偏移X] [偏移Y] [偏移Z] [速度]
```

### 参数说明
- **粒子类型** - Minecraft 粒子名称（必填）
- **数量** - 粒子数量，默认 1（可选）
- **偏移X/Y/Z** - 粒子散布范围，默认 0（可选）
- **速度** - 粒子移动速度，默认 0（可选）

### 使用示例

#### 1. 简单粒子效果
```yaml
actions:
  enter: |-
    particle FLAME
```

#### 2. 大量粒子效果
```yaml
actions:
  enter: |-
    particle PORTAL 50 2 2 2 0.5
```

#### 3. 持续粒子效果（配合 tick）
```yaml
actions:
  tick: |-
    particle FLAME 10 1 1 1 0.1
tick-period: 10
```

### 常用粒子列表

| 粒子名称 | 说明 | 适用场景 |
|---------|------|---------|
| `FLAME` | 火焰 | 危险区域、熔岩区 |
| `PORTAL` | 传送门粒子 | 传送点、魔法区 |
| `HEART` | 爱心 | 安全区、治疗区 |
| `VILLAGER_HAPPY` | 村民开心 | 友好区域 |
| `VILLAGER_ANGRY` | 村民生气 | 危险警告 |
| `ENCHANTMENT_TABLE` | 附魔台粒子 | 魔法区域 |
| `SMOKE_NORMAL` | 烟雾 | 工业区、战斗区 |
| `EXPLOSION_NORMAL` | 爆炸粒子 | 战斗区域 |
| `REDSTONE` | 红石粒子 | 红石实验室 |
| `NOTE` | 音符 | 音乐区域 |
| `DRAGON_BREATH` | 龙息 | Boss区域 |
| `END_ROD` | 末地烛粒子 | 末地区域 |
| `TOTEM` | 图腾粒子 | 保护区域 |

---

## 💬 聊天命令

### 1. tell - 发送消息
```yaml
tell "&a这是一条消息"
```

### 2. title - 显示标题
```yaml
title "&6主标题" "&e副标题"
```

### 3. actionbar - 显示动作栏
```yaml
actionbar "&a这是动作栏消息"
```

---

## 🎮 玩家命令

### 1. effect - 给予药水效果
```yaml
# 语法：effect give <效果类型> <持续时间(秒)> <等级>
effect give speed 10 1
effect give regeneration 999999 1
```

### 2. give - 给予物品
```yaml
# 语法：give <物品类型> [数量]
give diamond 1
give iron_ingot 64
```

### 3. tp - 传送玩家
```yaml
# 语法：tp <x> <y> <z>
tp 0 64 0
```

### 4. command - 执行命令
```yaml
# 以玩家身份执行命令
command "say 你好"

# 以控制台身份执行命令
sudo "give @p diamond 1"
```

---

## 🔧 逻辑控制

### 1. if - 条件判断
```yaml
if check perm *vip then {
  tell "&a你是VIP！"
} else {
  tell "&c你不是VIP"
}
```

### 2. check - 权限检查
```yaml
check perm *admin
```

---

## 📊 变量使用

PlayerArea 自动注入以下变量：

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `player` | 当前玩家 | - |
| `area_id` | 区域ID | "spawn" |
| `area_priority` | 区域优先级 | 10 |
| `world` | 玩家所在世界 | - |
| `location` | 玩家位置 | - |

### 使用示例
```yaml
actions:
  enter: |-
    tell "&a你进入了区域: &e{{ area_id }}"
    tell "&7优先级: &e{{ area_priority }}"
```

---

## 🎯 完整示例

### 示例1：魔法区域（粒子+音效）
```yaml
magic_zone:
  priority: 10
  position:
    shape: CUBOID
    world: "world"
    position1: "0, 64, 0"
    position2: "50, 100, 50"
  actions:
    enter: |-
      tell "&d欢迎来到魔法区域！"
      title "&5&l魔法区域" "&d感受魔法的力量"
      sound BLOCK_PORTAL_AMBIENT 1.0 1.0
      particle PORTAL 100 3 3 3 1.0
      effect give regeneration 999999 1
    
    leave: |-
      tell "&7你离开了魔法区域"
      effect clear regeneration
    
    tick: |-
      particle ENCHANTMENT_TABLE 20 2 2 2 0.5
      actionbar "&d魔法区域 &7| &5魔力充盈"
  
  tick-period: 20
```

### 示例2：战斗竞技场（完整反馈）
```yaml
battle_arena:
  priority: 15
  position:
    shape: CUBOID
    world: "world"
    position1: "100, 64, 100"
    position2: "200, 100, 200"
  actions:
    enter: |-
      tell "&c进入战斗竞技场！"
      title "&c&l竞技场" "&e准备战斗！"
      sound ENTITY_ENDER_DRAGON_GROWL 1.0 1.0
      particle EXPLOSION_NORMAL 50 5 5 5 0.1
    
    damage: |-
      sound ENTITY_PLAYER_HURT 1.0 1.0
      particle REDSTONE 10 0.5 0.5 0.5 0
    
    kill: |-
      tell "&a&l击杀！"
      sound ENTITY_PLAYER_LEVELUP 1.0 1.0
      particle TOTEM 100 2 2 2 0.5
      give diamond 1
    
    death: |-
      tell "&c你在竞技场中阵亡了"
      sound ENTITY_LIGHTNING_BOLT_THUNDER 0.5 0.8
  
  flags:
    pvp: true
```

---

## 📚 更多资源

- [TabooLib Kether 官方文档](http://kether.tabooproject.org/)
- [Minecraft 音效列表](https://minecraft.fandom.com/wiki/Sounds.json)
- [Minecraft 粒子列表](https://minecraft.fandom.com/wiki/Particles)

---

**提示**：所有命令都支持 `&` 颜色代码，会自动转换为 Minecraft 颜色！

