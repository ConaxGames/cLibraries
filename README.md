# 🌍 cLibraries
Conax Libraries is a development library for Bukkit plugins.
It provides a number of utilities and extended APIs which help to speed up development and reduce boilerplate code.

## 🔗 Useful Links
* **ConaxGames Website** - <https://www.conaxgames.com>
* **ConaxGames Discord** - <https://discord.gg/WMsVB39eCQ>

## 🛡️ How to implement
Add to your maven pom.xml as a dependency. 
Ensure the scope is set to compile.
```
    <dependency>
        <groupId>com.conaxgames</groupId>
        <artifactId>clibraries-plugin</artifactId>
        <version>LATEST</version>
        <scope>compile</scope>
    </dependency>
```

Remember that you must shade cLibraries into the plugin you are working on with relocation!
This can be done by using `maven-shade-plugin` inside your build configuration.
```
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

## ⛏️ How to integrate:
### onEnable inside your Java Plugin
`library = new LibraryPlugin().onEnable(...);`

### onDisable inside your Java Plugin
2. `library.onDisable();`