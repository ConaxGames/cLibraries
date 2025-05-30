package com.conaxgames.libraries.message;

import com.google.common.collect.ImmutableMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.apache.commons.lang.WordUtils;

import java.util.Map;

public class ItemNameUtil {
//    private static final Map<String,String> map = ImmutableMap.<String,String>builder()
//            .put("1", "Stone")
//            .put("1:1", "Granite")
//            .put("1:2", "Polished Granite")
//            .put("1:3", "Diorite")
//            .put("1:4", "Polished Diorite")
//            .put("1:5", "Andesite")
//            .put("1:6", "Polished Andesite")
//            .put("2", "Grass Block")
//            .put("3", "Dirt")
//            .put("3:1", "Coarse Dirt")
//            .put("3:2", "Podzol")
//            .put("4", "Cobblestone")
//            .put("5", "Oak Wood Planks")
//            .put("5:1", "Spruce Wood Planks")
//            .put("5:2", "Birch Wood Planks")
//            .put("5:3", "Jungle Wood Planks")
//            .put("5:4", "Acacia Wood Planks")
//            .put("5:5", "Dark Oak Wood Planks")
//            .put("6", "Oak Sapling")
//            .put("6:1", "Spruce Sapling")
//            .put("6:2", "Birch Sapling")
//            .put("6:3", "Jungle Sapling")
//            .put("6:4", "Acacia Sapling")
//            .put("6:5", "Dark Oak Sapling")
//            .put("7", "Bedrock")
//            .put("8", "Water (No Spread)")
//            .put("9", "Water")
//            .put("10", "Lava (No Spread)")
//            .put("11", "Lava")
//            .put("12", "Sand")
//            .put("12:1", "Red Sand")
//            .put("13", "Gravel")
//            .put("14", "Gold Ore")
//            .put("15", "Iron Ore")
//            .put("16", "Coal Ore")
//            .put("17", "Oak Wood")
//            .put("17:1", "Spruce Wood")
//            .put("17:2", "Birch Wood")
//            .put("17:3", "Jungle Wood")
//            .put("18", "Oak Leaves")
//            .put("18:1", "Spruce Leaves")
//            .put("18:2", "Birch Leaves")
//            .put("18:3", "Jungle Leaves")
//            .put("19", "Sponge")
//            .put("19:1", "Wet Sponge")
//            .put("20", "Glass")
//            .put("21", "Lapis Lazuli Ore")
//            .put("22", "Lapis Lazuli Block")
//            .put("23", "Dispenser")
//            .put("24", "Sandstone")
//            .put("24:1", "Chiseled Sandstone")
//            .put("24:2", "Smooth Sandstone")
//            .put("25", "Note Block")
//            .put("26", "Bed")
//            .put("27", "Powered Rail")
//            .put("28", "Detector Rail")
//            .put("29", "Sticky Piston")
//            .put("30", "Web")
//            .put("31", "Shrub")
//            .put("31:1", "Grass")
//            .put("31:2", "Fern")
//            .put("32", "Dead Bush")
//            .put("33", "Piston")
//            .put("34", "Piston (Head)")
//            .put("35", "Wool")
//            .put("35:1", "Orange Wool")
//            .put("35:2", "Magenta Wool")
//            .put("35:3", "Light Blue Wool")
//            .put("35:4", "Yellow Wool")
//            .put("35:5", "Lime Wool")
//            .put("35:6", "Pink Wool")
//            .put("35:7", "Gray Wool")
//            .put("35:8", "Light Gray Wool")
//            .put("35:9", "Cyan Wool")
//            .put("35:10", "Purple Wool")
//            .put("35:11", "Blue Wool")
//            .put("35:12", "Brown Wool")
//            .put("35:13", "Green Wool")
//            .put("35:14", "Red Wool")
//            .put("35:15", "Black Wool")
//            .put("37", "Dandelion")
//            .put("38", "Rose")
//            .put("38:1", "Blue Orchid")
//            .put("38:2", "Allium")
//            .put("38:3", "Azure Bluet")
//            .put("38:4", "Red Tulip")
//            .put("38:5", "Orange Tulip")
//            .put("38:6", "White Tulip")
//            .put("38:7", "Pink Tulip")
//            .put("38:8", "Oxeye Daisy")
//            .put("39", "Brown Mushroom")
//            .put("40", "Red Mushroom")
//            .put("41", "Gold Block")
//            .put("42", "Iron Block")
//            .put("43", "Stone Slab (Double)")
//            .put("43:1", "Sandstone Slab (Double)")
//            .put("43:2", "Wooden Slab (Double)")
//            .put("43:3", "Cobblestone Slab (Double)")
//            .put("43:4", "Brick Slab (Double)")
//            .put("43:5", "Stone Brick Slab (Double)")
//            .put("43:6", "Nether Brick Slab (Double)")
//            .put("43:7", "Quartz Slab (Double)")
//            .put("43:8", "Smooth Stone Slab (Double)")
//            .put("43:9", "Smooth Sandstone Slab (Double)")
//            .put("44", "Stone Slab")
//            .put("44:1", "Sandstone Slab")
//            .put("44:2", "Wooden Slab")
//            .put("44:3", "Cobblestone Slab")
//            .put("44:4", "Brick Slab")
//            .put("44:5", "Stone Brick Slab")
//            .put("44:6", "Nether Brick Slab")
//            .put("44:7", "Quartz Slab")
//            .put("45", "Brick")
//            .put("46", "TNT")
//            .put("47", "Bookcase")
//            .put("48", "Moss Stone")
//            .put("49", "Obsidian")
//            .put("50", "Torch")
//            .put("51", "Fire")
//            .put("52", "Mob Spawner")
//            .put("53", "Oak Wood Stairs")
//            .put("54", "Chest")
//            .put("55", "Redstone Wire")
//            .put("56", "Diamond Ore")
//            .put("57", "Diamond Block")
//            .put("58", "Crafting Table")
//            .put("59", "Wheat (Crop)")
//            .put("60", "Farmland")
//            .put("61", "Furnace")
//            .put("62", "Furnace (Smelting)")
//            .put("63", "Sign (Block)")
//            .put("64", "Wood Door (Block)")
//            .put("65", "Ladder")
//            .put("66", "Rails")
//            .put("67", "Stone Stairs")
//            .put("68", "Sign (Wall Block)")
//            .put("69", "Lever")
//            .put("70", "Pressure Plate")
//            .put("71", "Iron Door (Block)")
//            .put("72", "Pressure Plate")
//            .put("73", "Redstone Ore")
//            .put("74", "Redstone Ore (Glowing)")
//            .put("75", "Redstone Torch (Off)")
//            .put("76", "Redstone Torch")
//            .put("77", "Button")
//            .put("78", "Snow")
//            .put("79", "Ice")
//            .put("80", "Snow Block")
//            .put("81", "Cactus")
//            .put("82", "Clay Block")
//            .put("83", "Sugar Cane (Block)")
//            .put("84", "Jukebox")
//            .put("85", "Fence")
//            .put("86", "Pumpkin")
//            .put("87", "Netherrack")
//            .put("88", "Soul Sand")
//            .put("89", "Glowstone")
//            .put("90", "Portal")
//            .put("91", "Jack-O-Lantern")
//            .put("92", "Cake (Block)")
//            .put("93", "Redstone Repeater (Block Off)")
//            .put("94", "Redstone Repeater (Block On)")
//            .put("95", "Stained Glass")
//            .put("96", "Wooden Trapdoor")
//            .put("97", "Stone Monster Egg")
//            .put("97:1", "Cobblestone Monster Egg")
//            .put("97:2", "Stone Brick Monster Egg")
//            .put("97:3", "Mossy Stone Brick Monster Egg")
//            .put("97:4", "Cracked Stone Brick Monster Egg")
//            .put("97:5", "Chiseled Stone Brick Monster Egg")
//            .put("98", "Stone Bricks")
//            .put("98:1", "Mossy Stone Bricks")
//            .put("98:2", "Cracked Stone Bricks")
//            .put("98:3", "Chiseled Stone Bricks")
//            .put("99", "Brown Mushroom (Block)")
//            .put("100", "Red Mushroom (Block)")
//            .put("101", "Iron Bars")
//            .put("102", "Glass Pane")
//            .put("103", "Melon (Block)")
//            .put("104", "Pumpkin Vine")
//            .put("105", "Melon Vine")
//            .put("106", "Vines")
//            .put("107", "Fence Gate")
//            .put("108", "Brick Stairs")
//            .put("109", "Stone Brick Stairs")
//            .put("110", "Mycelium")
//            .put("111", "Lily Pad")
//            .put("112", "Nether Brick")
//            .put("113", "Nether Brick Fence")
//            .put("114", "Nether Brick Stairs")
//            .put("115", "Nether Wart")
//            .put("116", "Enchantment Table")
//            .put("117", "Brewing Stand (Block)")
//            .put("118", "Cauldron (Block)")
//            .put("119", "End Portal")
//            .put("120", "End Portal Frame")
//            .put("121", "End Stone")
//            .put("122", "Dragon Egg")
//            .put("123", "Redstone Lamp (Inactive)")
//            .put("124", "Redstone Lamp (Active)")
//            .put("125", "Double Wood Slab")
//            .put("126", "Oak Wood Slab")
//            .put("126:1", "Spruce Wood Slab")
//            .put("126:2", "Birch Slab")
//            .put("126:3", "Jungle Slab")
//            .put("126:4", "Acacia Wood Slab")
//            .put("126:5", "Dark Oak Wood Slab")
//            .put("127", "Cocoa Plant")
//            .put("128", "Sandstone Stairs")
//            .put("129", "Emerald Ore")
//            .put("130", "Ender Chest")
//            .put("131", "Tripwire Hook")
//            .put("132", "Tripwire")
//            .put("133", "Emerald Block")
//            .put("134", "Spruce Wood Stairs")
//            .put("135", "Birch Wood Stairs")
//            .put("136", "Jungle Wood Stairs")
//            .put("137", "Command Block")
//            .put("138", "Beacon Block")
//            .put("139", "Cobblestone Wall")
//            .put("139:1", "Mossy Cobblestone Wall")
//            .put("140", "Flower Pot")
//            .put("141", "Carrots")
//            .put("142", "Potatoes")
//            .put("143", "Button")
//            .put("144", "Head")
//            .put("145", "Anvil")
//            .put("146", "Trapped Chest")
//            .put("147", "Weighted Pressure Plate (Light)")
//            .put("148", "Weighted Pressure Plate (Heavy)")
//            .put("149", "Redstone Comparator (inactive)")
//            .put("150", "Redstone Comparator (active)")
//            .put("151", "Daylight Sensor")
//            .put("152", "Redstone Block")
//            .put("153", "Nether Quartz Ore")
//            .put("154", "Hopper")
//            .put("155", "Quartz Block")
//            .put("155:1", "Chiseled Quartz Block")
//            .put("155:2", "Pillar Quartz Block")
//            .put("156", "Quartz Stairs")
//            .put("157", "Activator Rail")
//            .put("158", "Dropper")
//            .put("159", "Stained Clay")
//            .put("160", "Stained Glass Pane")
//            .put("161", "Acacia Leaves")
//            .put("161:1", "Dark Oak Leaves")
//            .put("162", "Acacia Wood")
//            .put("162:1", "Dark Oak Wood")
//            .put("163", "Acacia Wood Stairs")
//            .put("164", "Dark Oak Wood Stairs")
//            .put("165", "Slime Block")
//            .put("166", "Barrier")
//            .put("167", "Iron Trapdoor")
//            .put("168", "Prismarine")
//            .put("168:1", "Prismarine Bricks")
//            .put("168:2", "Dark Prismarine")
//            .put("169", "Sea Lantern")
//            .put("170", "Hay Block")
//            .put("171", "Carpet")
//            .put("172", "Hardened Clay")
//            .put("173", "Block of Coal")
//            .put("174", "Packed Ice")
//            .put("175", "Sunflower")
//            .put("175:1", "Lilac")
//            .put("175:2", "Double Tallgrass")
//            .put("175:3", "Large Fern")
//            .put("175:4", "Rose Bush")
//            .put("175:5", "Peony")
//            .put("256", "Iron Shovel")
//            .put("257", "Iron Pickaxe")
//            .put("258", "Iron Axe")
//            .put("259", "Flint and Steel")
//            .put("260", "Apple")
//            .put("261", "Bow")
//            .put("262", "Arrow")
//            .put("263", "Coal")
//            .put("263:1", "Charcoal")
//            .put("264", "Diamond")
//            .put("265", "Iron Ingot")
//            .put("266", "Gold Ingot")
//            .put("267", "Iron Sword")
//            .put("268", "Wooden Sword")
//            .put("269", "Wooden Shovel")
//            .put("270", "Wooden Pickaxe")
//            .put("271", "Wooden Axe")
//            .put("272", "Stone Sword")
//            .put("273", "Stone Shovel")
//            .put("274", "Stone Pickaxe")
//            .put("275", "Stone Axe")
//            .put("276", "Diamond Sword")
//            .put("277", "Diamond Shovel")
//            .put("278", "Diamond Pickaxe")
//            .put("279", "Diamond Axe")
//            .put("280", "Stick")
//            .put("281", "Bowl")
//            .put("282", "Mushroom Stew")
//            .put("283", "Gold Sword")
//            .put("284", "Gold Shovel")
//            .put("285", "Gold Pickaxe")
//            .put("286", "Gold Axe")
//            .put("287", "String")
//            .put("288", "Feather")
//            .put("289", "Gunpowder")
//            .put("290", "Wooden Hoe")
//            .put("291", "Stone Hoe")
//            .put("292", "Iron Hoe")
//            .put("293", "Diamond Hoe")
//            .put("294", "Gold Hoe")
//            .put("295", "Seeds")
//            .put("296", "Wheat")
//            .put("297", "Bread")
//            .put("298", "Leather Helmet")
//            .put("299", "Leather Chestplate")
//            .put("300", "Leather Leggings")
//            .put("301", "Leather Boots")
//            .put("302", "Chainmail Helmet")
//            .put("303", "Chainmail Chestplate")
//            .put("304", "Chainmail Leggings")
//            .put("305", "Chainmail Boots")
//            .put("306", "Iron Helmet")
//            .put("307", "Iron Chestplate")
//            .put("308", "Iron Leggings")
//            .put("309", "Iron Boots")
//            .put("310", "Diamond Helmet")
//            .put("311", "Diamond Chestplate")
//            .put("312", "Diamond Leggings")
//            .put("313", "Diamond Boots")
//            .put("314", "Gold Helmet")
//            .put("315", "Gold Chestplate")
//            .put("316", "Gold Leggings")
//            .put("317", "Gold Boots")
//            .put("318", "Flint")
//            .put("319", "Raw Porkchop")
//            .put("320", "Cooked Porkchop")
//            .put("321", "Painting")
//            .put("322", "Gold Apple")
//            .put("322:1", "Gold Apple (Enchanted)")
//            .put("323", "Sign")
//            .put("324", "Wooden Door")
//            .put("325", "Bucket")
//            .put("326", "Water Bucket")
//            .put("327", "Lava Bucket")
//            .put("328", "Minecart")
//            .put("329", "Saddle")
//            .put("330", "Iron Door")
//            .put("331", "Redstone")
//            .put("332", "Snowball")
//            .put("333", "Boat")
//            .put("334", "Leather")
//            .put("335", "Milk Bucket")
//            .put("336", "Brick")
//            .put("337", "Clay")
//            .put("338", "Sugar Cane")
//            .put("339", "Paper")
//            .put("340", "Book")
//            .put("341", "Slime Ball")
//            .put("342", "Storage Minecart")
//            .put("343", "Powered Minecart")
//            .put("344", "Egg")
//            .put("345", "Compass")
//            .put("346", "Fishing Rod")
//            .put("347", "Watch")
//            .put("348", "Glowstone Dust")
//            .put("349", "Raw Fish")
//            .put("349:1", "Raw Salmon")
//            .put("349:2", "Clownfish")
//            .put("349:3", "Pufferfish")
//            .put("350", "Cooked Fish")
//            .put("350:1", "Cooked Salmon")
//            .put("351", "Ink Sack [Black Dye]")
//            .put("351:1", "Rose Red [Red Dye]")
//            .put("351:2", "Cactus Green [Green Dye]")
//            .put("351:3", "Cocoa Bean [Brown Dye]")
//            .put("351:4", "Lapis Lazuli [Blue Dye]")
//            .put("351:5", "Purple Dye")
//            .put("351:6", "Cyan Dye")
//            .put("351:7", "Light Gray Dye")
//            .put("351:8", "Gray Dye")
//            .put("351:9", "Pink Dye")
//            .put("351:10", "Lime Dye")
//            .put("351:11", "Dandelion Yellow [Yellow Dye]")
//            .put("351:12", "Light Blue Dye")
//            .put("351:13", "Magenta Dye")
//            .put("351:14", "Orange Dye")
//            .put("351:15", "Bone Meal [White Dye]")
//            .put("352", "Bone")
//            .put("353", "Sugar")
//            .put("354", "Cake")
//            .put("355", "Bed")
//            .put("356", "Redstone Repeater")
//            .put("357", "Cookie")
//            .put("358", "Map")
//            .put("359", "Shears")
//            .put("360", "Melon")
//            .put("361", "Pumpkin Seeds")
//            .put("362", "Melon Seeds")
//            .put("363", "Raw Beef")
//            .put("364", "Steak")
//            .put("365", "Raw Chicken")
//            .put("366", "Roast Chicken")
//            .put("367", "Rotten Flesh")
//            .put("368", "Ender Pearl")
//            .put("369", "Blaze Rod")
//            .put("370", "Ghast Tear")
//            .put("371", "Gold Nugget")
//            .put("372", "Nether Wart")
//            .put("373", "Water Bottle")
//            .put("373:16", "Awkward Potion")
//            .put("373:32", "Thick Potion")
//            .put("373:64", "Mundane Potion")
//            .put("373:8193", "Regeneration Potion (0:45)")
//            .put("373:8194", "Swiftness Potion (3:00)")
//            .put("373:8195", "Fire Resistance Potion (3:00)")
//            .put("373:8196", "Poison Potion (0:45)")
//            .put("373:8197", "Healing Potion")
//            .put("373:8200", "Weakness Potion (1:30)")
//            .put("373:8201", "Strength Potion (3:00)")
//            .put("373:8202", "Slowness Potion (1:30)")
//            .put("373:8203", "Potion of Leaping (3:00)")
//            .put("373:8204", "Harming Potion")
//            .put("373:8225", "Regeneration Potion II (0:22)")
//            .put("373:8226", "Swiftness Potion II (1:30)")
//            .put("373:8228", "Poison Potion II (0:22)")
//            .put("373:8229", "Healing Potion II")
//            .put("373:8230", "Night Vision Potion (3:00)")
//            .put("373:8233", "Strength Potion II (1:30)")
//            .put("373:8235", "Potion of Leaping (1:30)")
//            .put("373:8236", "Harming Potion II")
//            .put("373:8237", "Water Breathing Potion (3:00)")
//            .put("373:8238", "Invisibility Potion (3:00)")
//            .put("373:8257", "Regeneration Potion (2:00)")
//            .put("373:8258", "Swiftness Potion (8:00)")
//            .put("373:8259", "Fire Resistance Potion (8:00)")
//            .put("373:8260", "Poison Potion (2:00)")
//            .put("373:8262", "Night Vision Potion (8:00)")
//            .put("373:8264", "Weakness Potion (4:00)")
//            .put("373:8265", "Strength Potion (8:00)")
//            .put("373:8266", "Slowness Potion (4:00)")
//            .put("373:8269", "Water Breathing Potion (8:00)")
//            .put("373:8270", "Invisibility Potion (8:00)")
//            .put("373:16378", "Fire Resistance Splash (2:15)")
//            .put("373:16385", "Regeneration Splash (0:33)")
//            .put("373:16386", "Swiftness Splash (2:15)")
//            .put("373:16388", "Poison Splash (0:33)")
//            .put("373:16389", "Healing Splash")
//            .put("373:16392", "Weakness Splash (1:07)")
//            .put("373:16393", "Strength Splash (2:15)")
//            .put("373:16394", "Slowness Splash (1:07)")
//            .put("373:16396", "Harming Splash")
//            .put("373:16418", "Swiftness Splash II (1:07)")
//            .put("373:16420", "Poison Splash II (0:16)")
//            .put("373:16421", "Healing Splash II")
//            .put("373:16422", "Night Vision Splash (2:15)")
//            .put("373:16425", "Strength Splash II (1:07)")
//            .put("373:16428", "Harming Splash II")
//            .put("373:16429", "Water Breathing Splash (2:15)")
//            .put("373:16430", "Invisibility Splash (2:15)")
//            .put("373:16449", "Regeneration Splash (1:30)")
//            .put("373:16450", "Swiftness Splash (6:00)")
//            .put("373:16451", "Fire Resistance Splash (6:00)")
//            .put("373:16452", "Poison Splash (1:30)")
//            .put("373:16454", "Night Vision Splash (6:00)")
//            .put("373:16456", "Weakness Splash (3:00)")
//            .put("373:16457", "Strength Splash (6:00)")
//            .put("373:16458", "Slowness Splash (3:00)")
//            .put("373:16461", "Water Breathing Splash (6:00)")
//            .put("373:16462", "Invisibility Splash (6:00)")
//            .put("373:16471", "Regeneration Splash II (0:16)")
//            .put("374", "Glass Bottle")
//            .put("375", "Spider Eye")
//            .put("376", "Fermented Spider Eye")
//            .put("377", "Blaze Powder")
//            .put("378", "Magma Cream")
//            .put("379", "Brewing Stand")
//            .put("380", "Cauldron")
//            .put("381", "Eye of Ender")
//            .put("382", "Glistering Melon")
//            .put("383", "Spawn Egg")
//            .put("383:50", "Spawn Creeper")
//            .put("383:51", "Spawn Skeleton")
//            .put("383:52", "Spawn Spider")
//            .put("383:54", "Spawn Zombie")
//            .put("383:55", "Spawn Slime")
//            .put("383:56", "Spawn Ghast")
//            .put("383:57", "Spawn Pigman")
//            .put("383:58", "Spawn Enderman")
//            .put("383:59", "Spawn Cave Spider")
//            .put("383:60", "Spawn Silverfish")
//            .put("383:61", "Spawn Blaze")
//            .put("383:62", "Spawn Magma Cube")
//            .put("383:65", "Spawn Bat")
//            .put("383:66", "Spawn Witch")
//            .put("383:67", "Spawn Endermite")
//            .put("383:68", "Spawn Guardian")
//            .put("383:90", "Spawn Pig")
//            .put("383:91", "Spawn Sheep")
//            .put("383:92", "Spawn Cow")
//            .put("383:93", "Spawn Chicken")
//            .put("383:94", "Spawn Squid")
//            .put("383:95", "Spawn Wolf")
//            .put("383:96", "Spawn Mooshroom")
//            .put("383:98", "Spawn Ocelot")
//            .put("383:100", "Spawn Horse")
//            .put("383:101", "Spawn Rabbit")
//            .put("383:120", "Spawn Villager")
//            .put("384", "Bottle o' Enchanting")
//            .put("385", "Fire Charge")
//            .put("386", "Book and Quill")
//            .put("387", "Written Book")
//            .put("388", "Emerald")
//            .put("389", "Item Frame")
//            .put("390", "Flower Pot")
//            .put("391", "Carrot")
//            .put("392", "Potato")
//            .put("393", "Baked Potato")
//            .put("394", "Poisonous Potato")
//            .put("395", "Empty Map")
//            .put("396", "Golden Carrot")
//            .put("397", "Skull Item")
//            .put("397:0", "Skeleton Skull")
//            .put("397:1", "Wither Skeleton Skull")
//            .put("397:2", "Zombie Head")
//            .put("373:3", "Head")
//            .put("373:4", "Creeper Head")
//            .put("398", "Carrot on a Stick")
//            .put("399", "Nether Star")
//            .put("400", "Pumpkin Pie")
//            .put("401", "Firework Rocket")
//            .put("402", "Firework Star")
//            .put("403", "Enchanted Book")
//            .put("404", "Redstone Comparator")
//            .put("405", "Nether Brick")
//            .put("406", "Nether Quartz")
//            .put("407", "Minecart with TNT")
//            .put("408", "Minecart with Hopper")
//            .put("409", "Prismarine Shard")
//            .put("410", "Prismarine Crystals")
//            .put("411", "Raw Rabbit")
//            .put("412", "Cooked Rabbit")
//            .put("413", "Rabbit Stew")
//            .put("414", "Rabbit Foot")
//            .put("415", "Rabbit Hide")
//            .put("417", "Iron Horse Armor")
//            .put("418", "Gold Horse Armor")
//            .put("419", "Diamond Horse Armor")
//            .put("420", "Lead")
//            .put("421", "Name Tag")
//            .put("422", "Minecart with Command Block")
//            .put("423", "Raw Mutton")
//            .put("424", "Cooked Mutton")
//            .put("2256", "Music Disk (13)")
//            .put("2257", "Music Disk (Cat)")
//            .put("2258", "Music Disk (Blocks)")
//            .put("2259", "Music Disk (Chirp)")
//            .put("2260", "Music Disk (Far)")
//            .put("2261", "Music Disk (Mall)")
//            .put("2262", "Music Disk (Mellohi)")
//            .put("2263", "Music Disk (Stal)")
//            .put("2264", "Music Disk (Strad)")
//            .put("2265", "Music Disk (Ward)")
//            .put("2266", "Music Disk (11)")
//            .put("2267", "Music Disk (wait)")
//            .build();
//
//    /**
//     * Given an item stack, return a friendly printable name for the item, as
//     * the (English-language) vanilla Minecraft client would display it.
//     *
//     * @param stack the item stack
//     * @return a friendly printable name for the item
//     */
//    public static String lookup(ItemStack stack) {
//        if (stack.hasItemMeta()) {
//            ItemMeta meta = stack.getItemMeta();
//            if (meta.getDisplayName() != null) {
//                return meta.getDisplayName();
//            } else if (meta instanceof BookMeta) {
//                return ((BookMeta)meta).getTitle();
//            }
//        }
//
//        String result;
//        String key = Integer.toString(stack.getTypeId());
//        Material mat = stack.getType();
//        if ((mat == Material.WOOL || mat == Material.CARPET) && stack.getDurability() == 0) {
//            // special case: white wool/carpet is just called "Wool" or "Carpet"
//            result = map.get(key);
//        } else if (mat == Material.WOOL || mat == Material.CARPET || mat == Material.STAINED_CLAY || mat == Material.STAINED_GLASS || mat == Material.STAINED_GLASS_PANE) {
//            DyeColor dc = DyeColor.getByWoolData((byte)stack.getDurability());
//            result = dc == null ? map.get(key) : WordUtils.capitalizeFully(dc.toString().replace("_", " ")) + " " + map.get(key);
//        } else if (mat == Material.LEATHER_HELMET || mat == Material.LEATHER_CHESTPLATE || mat == Material.LEATHER_LEGGINGS || mat == Material.LEATHER_BOOTS) {
//            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) stack.getItemMeta();
//            DyeColor dc = DyeColor.getByColor(leatherArmorMeta.getColor());
//            result = dc == null ? map.get(key) : WordUtils.capitalizeFully(dc.toString()).replace("_", " ") + " " + map.get(key);
//        } else if (stack.getDurability() != 0) {
//            result = map.get(key + ":" + stack.getDurability());
//            if (result == null) {
//                result = map.get(key);
//            }
//        } else {
//            result = map.containsKey(key) ? map.get(key) : stack.getType().toString();
//        }
//
//        return result;
//    }
//
//    /**
//     * Given an item stack return a friendly name for the item, in the form
//     * "{amount} x {item-name}" where {amount} is the number of items in the
//     * stack and {item-name} is the return value of
//     * {@link #lookup(ItemStack)}}.
//     *
//     * @param stack the item stack
//     * @return a friendly printable name for the item, with amount information
//     */
//    public static String lookupWithAmount(ItemStack stack) {
//        String s = lookup(stack);
//        return CC.GRAY + stack.getAmount() + "x " + CC.WHITE + s;
//    }

    private static final Map<String,String> potionmap = ImmutableMap.<String,String>builder()
            .put("speed", "Speed")
            .put("slowness", "Slowness")
            .put("haste", "Haste")
            .put("mining_fatigue", "Mining Fatigue")
            .put("strength", "Strength")
            .put("instant_health", "Instant Health")
            .put("instant_damage", "Instant Damage")
            .put("jump_boost", "Jump Boost")
            .put("nausea", "Nausea")
            .put("regeneration", "Regeneration")
            .put("resistance", "Resistance")
            .put("fire_resistance", "Fire Resistance")
            .put("water_breathing", "Water Breathing")
            .put("invisibility", "Invisibility")
            .put("blindness", "Blindness")
            .put("night_vision", "Night Vision")
            .put("hunger", "Hunger")
            .put("weakness", "Weakness")
            .put("poison", "Poison")
            .put("wither", "Wither")
            .put("health_boost", "Health Boost")
            .put("absorption", "Absorption")
            .put("saturation", "Saturation")
            .build();

    public static String potionLookup(PotionEffectType potion) {
        String result;
        String key = potion.getName().toLowerCase();
        result = potionmap.get(key);
        
        if (result == null) {
            result = WordUtils.capitalizeFully(potion.getName().replace("_", " "));
        }
        
        return result;
    }

    private static final Map<String,String> enchantmentmap = ImmutableMap.<String,String>builder()
            .put("0", "Protection")
            .put("1", "Fire Protection")
            .put("2", "Feather Falling")
            .put("3", "Blast Protection")
            .put("4", "Projectile Protection")
            .put("5", "Respiration")
            .put("6", "Aqua Affinity")
            .put("7", "Thorns")
            .put("8", "Depth Strider")
            .put("16", "Sharpness")
            .put("17", "Smite")
            .put("18", "Bane of Arthropods")
            .put("19", "Knockback")
            .put("20", "Fire Aspect")
            .put("21", "Looting")
            .put("32", "Efficiency")
            .put("33", "Silk Touch")
            .put("34", "Unbreaking")
            .put("35", "Fortune")
            .put("48", "Power")
            .put("49", "Punch")
            .put("50", "Flame")
            .put("51", "Infinity")
            .put("61", "Luck of the Sea")
            .put("62", "Lure")
            .build();

    public static String enchantLookup(Enchantment enchantment) {
        String result;
        String key = enchantment.getName();
        result = enchantmentmap.get(key);
        return result;
    }
}

