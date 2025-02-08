
# cLibraries

**Overview**  
Conax Libraries (cLibraries) is a development library for Bukkit plugins. It offers various utilities and extended APIs designed to streamline development and reduce boilerplate code.

## Useful Links
- **ConaxGames Website:** [https://www.conaxgames.com](https://www.conaxgames.com)
- **ConaxGames Discord:** [https://discord.gg/fYZt22SmTp](https://discord.gg/fYZt22SmTp)

## Implementation

To use cLibraries in your project, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.conaxgames</groupId>
    <artifactId>clibraries</artifactId>
    <version>LATEST</version>
    <scope>compile</scope>
</dependency>
```

### Shading cLibraries

Because cLibraries must be shaded into your plugin to avoid conflicts, you should configure the [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/) with relocation in your project's `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.4.1</version>
    <executions>
        <execution>
            <id>shade</id>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <relocations>
            <relocation>
                <pattern>com.conaxgames.libraries</pattern>
                <shadedPattern>com.conaxgames.{x}.clib</shadedPattern>
            </relocation>
        </relocations>
    </configuration>
</plugin>
```

## Usage

### Initialization (onEnable)

Inside your plugin’s `onEnable` method, initialize the library by creating a new `LibraryPlugin` instance and invoking `onEnable()`:

```java
@Override
public void onEnable() {
    library = new LibraryPlugin().onEnable(this);
    // Your plugin-specific setup code...
}
```

### Shutdown (onDisable)

In your plugin’s `onDisable` method, gracefully shut down the library:

```java
@Override
public void onDisable() {
    library.onDisable();
    // Your plugin-specific teardown code...
}
```

---
