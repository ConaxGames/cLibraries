# cLibraries

A Bukkit/PaperMC plugin development framework.

## Installation

<details open>
<summary><b>Maven</b></summary>

```xml
<dependency>
    <groupId>com.conaxgames</groupId>
    <artifactId>clibraries</artifactId>
    <version>1.2.2</version>
    <scope>compile</scope>
</dependency>
```

</details>

<details>
<summary><b>Gradle</b></summary>

```groovy
dependencies {
    implementation 'com.conaxgames:clibraries:1.2.2'
}
```

</details>

Shade the library into your plugin to avoid conflicts.

## Usage

```java
public class YourPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        new LibraryPlugin().onEnable(
            this, 
            "§2", 
            "§7", 
            "pluginname", 
            "pluginname.permission"
        );
    }
    
    @Override
    public void onDisable() {
        LibraryPlugin.getInstance().onDisable();
    }
}
```

Access the library instance anywhere using `LibraryPlugin.getInstance()`.

## Supported APIs

- [Menu](src/main/java/com/conaxgames/libraries/menu/Menu.java) - Inventory GUI framework
- [Scheduler](src/main/java/com/conaxgames/libraries/util/scheduler/Scheduler.java) - Bukkit/Folia scheduling abstraction
- [Board](src/main/java/com/conaxgames/libraries/board/BoardManager.java) - Scoreboard management
- [Timer API](src/main/java/com/conaxgames/libraries/timer/Timer.java) - Cooldown timer system with event support
- [ItemBuilderUtil](src/main/java/com/conaxgames/libraries/util/ItemBuilderUtil.java) - Item builder utility
- [Player Inventory Snapshot](src/main/java/com/conaxgames/libraries/util/inventory/PlayerInventoryUtil.java) - Player inventory snapshot and restore
- [Configuration](src/main/java/com/conaxgames/libraries/config/CommentedConfiguration.java) - YAML configuration with comment preservation
- [CC](src/main/java/com/conaxgames/libraries/util/CC.java) - Chat color constants and translation (including hex)
- [Center](src/main/java/com/conaxgames/libraries/util/center/Center.java) - Pixel-based chat message centering
- [ClassUtils](src/main/java/com/conaxgames/libraries/util/ClassUtils.java) - Package class scanning and discovery utilities
- [XPUtil](src/main/java/com/conaxgames/libraries/util/XPUtil.java) - Experience point calculation and management utilities
- [ColorMaterialUtil](src/main/java/com/conaxgames/libraries/util/ColorMaterialUtil.java) - Chat color to wool, terracotta, and carpet material mapping
- [ProgressionBar](src/main/java/com/conaxgames/libraries/util/ProgressionBar.java) - Text-based progress bar builder for chat or scoreboards

## License

MIT License
