# cLibraries

## Overview
cLibraries is a comprehensive development framework for Bukkit/PaperMC plugins that provides a robust set of utilities, APIs, and abstractions to accelerate Minecraft server plugin development. This library is designed to reduce boilerplate code, streamline common development tasks, and provide consistent patterns for plugin architecture.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java: 1.8+](https://img.shields.io/badge/Java-1.8%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Paper: Support](https://img.shields.io/badge/Paper-Support-green.svg)](https://papermc.io/)

## Features

- **Module System**: A flexible module architecture allowing for modular plugin design
- **Command Framework**: Integration with ACF (Aikar's Command Framework) for powerful command registration
- **Scoreboard API**: Simple yet powerful scoreboard management system
- **Plugin Hooks**: Easy integration with other plugins through an extensible hooks system
- **Redis Integration**: Built-in Redis connectivity for cross-server communication
- **Menu/GUI Framework**: Streamlined inventory menu creation utilities
- **Configuration Framework**: Simplified YAML configuration management
- **Utility Classes**: Extensive collection of utility methods for common Minecraft plugin development tasks
- **Event Framework**: Enhanced event management systems
- **Timers & Countdowns**: Utilities for creating and managing in-game timers

## Installation

### Maven

```xml
<dependency>
    <groupId>com.conaxgames</groupId>
    <artifactId>clibraries</artifactId>
    <version>1.2.2</version>
    <scope>compile</scope>
</dependency>
```

Since cLibraries must be shaded into your plugin to avoid conflicts, configure the Maven Shade Plugin with relocation:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>1.2.2</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>com.conaxgames.libraries</pattern>
                        <shadedPattern>com.yourpackage.libs.clib</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Gradle

```groovy
dependencies {
    implementation 'com.conaxgames:clibraries:1.1.3'
}
```

Configure the Shadow plugin with relocation:

```groovy
shadowJar {
    relocate 'com.conaxgames.libraries', 'com.yourpackage.libs.clib'
}
```

## Integration

### Initialization

```java
public class YourPlugin extends JavaPlugin {
    
    private LibraryPlugin libraryPlugin;
    
    @Override
    public void onEnable() {
        // Initialize cLibraries
        this.libraryPlugin = new LibraryPlugin().onEnable(
            this,                  // Your plugin instance
            "YourDebugPrefix",     // Debug prefix
            "YourDebugSecondary",  // Secondary debug info
            "module",              // Module command alias
            "yourplugin.modules"   // Module command permission
        );
        
        // Your plugin initialization code
    }
    
    @Override
    public void onDisable() {
        // Properly shutdown cLibraries
        this.libraryPlugin.onDisable();
        
        // Your plugin shutdown code
    }
}
```

### Configuration

The library supports a settings.yml file that can be used to configure various aspects of the library's functionality.

```java
// Access settings
Settings settings = libraryPlugin.getSettings();
```

### Custom Scoreboard Implementation

```java
public class YourScoreboardAdapter extends BoardAdapter {
    
    @Override
    public String getTitle(Player player) {
        return "Your Server";
    }
    
    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add("&aPlayer: &f" + player.getName());
        lines.add("&aOnline: &f" + Bukkit.getOnlinePlayers().size());
        // Add more lines
        return lines;
    }
    
    @Override
    public long getInterval() {
        return 2L; // Update interval in ticks
    }
}

// Register your scoreboard adapter
YourScoreboardAdapter adapter = new YourScoreboardAdapter();
BoardManager boardManager = new BoardManager(libraryPlugin, adapter);
libraryPlugin.setBoardManager(boardManager);
```

## Advanced Usage

### Module Registration

```java
// Create a module
public class YourModule extends Module {
    
    public YourModule() {
        super("module-id", "Module Name", "Module description");
    }
    
    @Override
    public void onEnable() {
        // Module initialization
    }
    
    @Override
    public void onDisable() {
        // Module cleanup
    }
}

// Register the module
libraryPlugin.getModuleManager().registerModule(new YourModule());
```

### Command Registration

```java
// Create commands using ACF
@CommandAlias("yourcommand")
public class YourCommands extends BaseCommand {
    
    @Default
    public void onCommand(Player player) {
        player.sendMessage("Your command was executed!");
    }
    
    @Subcommand("reload")
    @CommandPermission("yourplugin.reload")
    public void onReload(CommandSender sender) {
        // Reload logic
        sender.sendMessage("Reloaded configuration!");
    }
}

// Register commands
libraryPlugin.getCommandRegistry().registerCommand(new YourCommands());
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Links
- [Issue Tracker](https://github.com/ConaxGames/cLibraries/issues)
- [ConaxGames Website](https://www.conaxgames.com)
- [ConaxGames Discord](https://discord.gg/fYZt22SmTp)
