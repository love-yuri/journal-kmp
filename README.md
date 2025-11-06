# 📔 正经人都不用的日记软件

> 一款基于 Kotlin Multiplatform 构建的跨平台日记应用，专注于提供流畅的书写体验与安全的数据管理
> 因为没有设备所以暂未支持所有苹果设备，如需ios平台支持请自行添加ios依赖。

<div>
    <img src="https://img.shields.io/badge/version-1.2.0-blue.svg" alt="version"/>
    <img src="https://img.shields.io/badge/Kotlin-2.2.20-purple.svg" alt="kotlin"/>
    <img src="https://img.shields.io/badge/Compose-1.8.2-green.svg" alt="compose"/>
    <img src="https://img.shields.io/badge/license-MIT-orange.svg" alt="license"/>
</div>

---

## ✨ 功能特性

- 📝 **日记管理** - 支持创建、编辑、删除日记，提供完整的记录统计功能
- 🌸 **优雅交互** - 丰富的高性能动画效果，人性化的编辑体验
- 🔒 **隐私保护** - 指纹/PIN 码认证，守护你的私密日记
- ☁️ **WebDAV 备份** - 自动/手动备份到 WebDAV 服务器，随时同步数据库文件
- 📱 **深度优化** - Android 平台深度适配，提供原生级体验

---

## 📝 版本历史

### v1.2.0
- 新增离开页面自动保存/更新功能
- 新增远程备份与同步功能

### v1.0.0 (2025-11-02)

🎉 首个正式版本发布

**核心功能**
- 日记的增删改查与统计分析
- WebDAV 云端备份与恢复
- 指纹/PIN 码身份验证
- Material Design 3 界面设计
- 流畅的动画与交互体验

---

## 📸 应用预览

<div align="center">
    <img src="doc/introduce.gif" alt="应用演示" width="300"/>
</div>

---

## 🛠 核心框架

| 依赖                    | 用途        |
|-----------------------|-----------|
| Compose Multiplatform | 跨平台 UI 框架 |
| Voyager               | 导航管理      |
| SQLDelight            | 数据库 ORM   |
| Retrofit              | 网络请求      |
| AndroidX Biometric    | 生物识别认证    |

---

## 🚀 快速开始

**环境要求**: JDK 21 或更高版本

```bash
# 克隆项目
git clone https://github.com/love-yuri/Journal.git
cd Journal

# 运行项目
./gradlew run

# 查看所有可用任务
./gradlew tasks
```

---

## 📂 项目结构

```bash
Journal/
├── composeApp/
│   ├── build.gradle.kts     # Gradle 配置文件
│   ├── local.properties     # 本地配置
│   └── src/
│       ├── androidMain/     # Android 平台实现
│       ├── commonMain/      # 共享代码
│       ├── commonTest/      # 单元测试
│       └── jvmMain/         # JVM 平台实现
├── doc/                     # 文档与素材
├── gradle/                  # Gradle 管理目录
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

**Made with ❤️ by [love-yuri](https://github.com/love-yuri)**

如果觉得不错，欢迎 ⭐ Star 支持一下
