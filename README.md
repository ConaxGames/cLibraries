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

## ⛏️ How to integrate:
### onEnable inside your Java Plugin
`library = new LibraryPlugin().onEnable(this, CC.RED, CC.WHITE);`

### onDisable inside your Java Plugin
2. `library.onDisable();`