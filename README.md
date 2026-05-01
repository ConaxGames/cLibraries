<!--suppress HtmlDeprecatedAttribute -->

<div align="center">
    <h2><i>cLibraries</i></h2>
    <h3>PaperMC helpers for Minecraft server plugins maintained by ConaxGames.</h3>
    <a href="https://github.com/ConaxGames/cLibraries/actions/workflows/uploader-and-publisher.yml"><img src="https://img.shields.io/github/actions/workflow/status/ConaxGames/cLibraries/uploader-and-publisher.yml?style=for-the-badge&logo=github" alt="CI"></a>
    <img src="https://img.shields.io/github/license/ConaxGames/cLibraries?style=for-the-badge&logo=github" alt="License">
    <a href="https://github.com/ConaxGames/cLibraries/releases"><img src="https://img.shields.io/github/downloads/ConaxGames/cLibraries/total.svg?style=for-the-badge&logo=github" alt="Downloads"></a>
    <a href="https://discord.com/invite/fYZt22SmTp"><img src="https://img.shields.io/badge/Discord-ConaxGames-5865F2?style=for-the-badge&logo=discord&logoColor=white" alt="Discord"></a>
</div>

<h3>Resources</h3>

- [Maven Central](https://central.sonatype.com/search?name=clibraries&namespace=com.conaxgames&type=maven)
    - [Repository directory](https://repo.maven.apache.org/maven2/com/conaxgames/clibraries/)
- [Releases](https://github.com/ConaxGames/cLibraries/releases/)
    - [Latest](https://github.com/ConaxGames/cLibraries/releases/latest)
- [Java source](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries)

<h3>Libraries</h3>

<i>Framework helpers shipped in the artifact (entry-point classes linked).</i>

- [Menu](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/menu/Menu.java) — Inventory GUI framework
- [Scheduler](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/scheduler/Scheduler.java) — Bukkit/Folia scheduling abstraction
- [BoardManager](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/board/BoardManager.java) — Scoreboard management
- [Timer](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/timer/Timer.java) — Cooldown timers with events
- [ItemBuilderUtil](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/ItemBuilderUtil.java) — Item builder utility
- [PlayerInventoryUtil](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/inventory/PlayerInventoryUtil.java) — Inventory snapshot and restore
- [CommentedConfiguration](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/config/CommentedConfiguration.java) — YAML with preserved comments
- [CC](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/CC.java) — Chat colors and translations (including hex)
- [Center](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/center/Center.java) — Pixel-based chat message centering
- [ClassUtils](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/ClassUtils.java) — Package scanning and discovery
- [XPUtil](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/XPUtil.java) — Experience helpers
- [ColorMaterialUtil](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/ColorMaterialUtil.java) — Chat colors to wool, terracotta, and carpet
- [ProgressionBar](https://github.com/ConaxGames/cLibraries/tree/main/src/main/java/com/conaxgames/libraries/util/ProgressionBar.java) — Progress bars for chat or scoreboards

<h3>Install</h3>

**Maven**

```xml
<dependency>
    <groupId>com.conaxgames</groupId>
    <artifactId>clibraries</artifactId>
    <version>1.4.0</version>
</dependency>
```

**Gradle**

```groovy
implementation 'com.conaxgames:clibraries:1.4.0'
```

Shade this library inside your artifact to avoid clashes; relocate `com.conaxgames` as needed ([Maven Shade](https://maven.apache.org/plugins/maven-shade-plugin/examples/class-relocation.html) · [Shadow](https://gradleup.com/shadow/configuration/relocation/)).

<h3>Bootstrap</h3>

```java
@Override
public void onEnable() {
    new LibraryPlugin().onEnable(this, "§2", "§7", "yourplugin", "yourplugin.permission");
}

@Override
public void onDisable() {
    LibraryPlugin.getInstance().onDisable();
}
```

Use `LibraryPlugin.getInstance()` anywhere after `onEnable`.

<h3>Credits</h3>

<i>clibraries ships against papermc tooling and owes its stability to upstream platform work:</i>

[Paper](https://papermc.io/software/paper) · [Folia](https://papermc.io/software/folia) · [Velocity](https://papermc.io/software/velocity) · [XSeries](https://github.com/CryptoMorin/XSeries)
