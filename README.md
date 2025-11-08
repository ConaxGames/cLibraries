# cLibraries

A Bukkit/PaperMC plugin development framework with utilities for modules, commands, scoreboards, menus, Redis, and more.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java: 1.8+](https://img.shields.io/badge/Java-1.8%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Paper: Support](https://img.shields.io/badge/Paper-Support-green.svg)](https://papermc.io/)

## Installation

**Maven:**
```xml
<dependency>
    <groupId>com.conaxgames</groupId>
    <artifactId>clibraries</artifactId>
    <version>1.2.2</version>
    <scope>compile</scope>
</dependency>
```

**Gradle:**
```groovy
dependencies {
    implementation 'com.conaxgames:clibraries:1.2.2'
}
```

**Important:** Shade the library into your plugin to avoid conflicts.

## Quick Start

```java
public class YourPlugin extends JavaPlugin {
    private LibraryPlugin libraryPlugin;
    
    @Override
    public void onEnable() {
        this.libraryPlugin = new LibraryPlugin().onEnable(
            this, "§2", "§7", "module", "yourplugin.modules"
        );
    }
    
    @Override
    public void onDisable() {
        this.libraryPlugin.onDisable();
    }
}
```

**Parameters:**
- `debugPrimary` - Color code for primary log messages (e.g., `"§2"` for dark green, `"§b"` for aqua)
- `debugSecondary` - Color code for secondary log messages (e.g., `"§7"` for gray)
- `moduleCommandAlias` - Command alias for module management (e.g., `"module"`)
- `moduleCommandPerm` - Package path for module commands (e.g., `"yourplugin.modules"`)

The logger will automatically format messages as `[YourPlugin] action: message` using the provided color codes.

## Features

- **Modules** - Modular plugin architecture
- **Commands** - ACF integration for command handling
- **Scoreboards** - Easy scoreboard management
- **Menus** - Inventory GUI framework
- **Redis** - Cross-server communication
- **Hooks** - Plugin integration system
- **Timers** - In-game timer utilities
- **Configuration** - YAML config management

## License

MIT License - see [LICENSE](LICENSE) file for details.
