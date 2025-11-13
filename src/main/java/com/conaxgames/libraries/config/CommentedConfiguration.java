package com.conaxgames.libraries.config;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.util.Pair;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommentedConfiguration extends YamlConfiguration {

    private final Map<String, String> configComments = new HashMap<>();
    private boolean creationFailure = false;

    public CommentedConfiguration() {
        try {
            this.options().parseComments(false);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Sync the config with another resource.
     * This method can be used as an auto updater for your config files.
     * @param file The file to save changes into if there are any.
     * @param resource The resource to sync with. Can be provided by JavaPlugin#getResource
     * @param ignoredSections An array of sections that will be ignored, and won't get updated
     *                        if they already exist in the config. If they are not in the
     *                        config, they will be synced with the resource's config.
     */
    public void syncWithConfig(File file, InputStream resource, String... ignoredSections) throws IOException {
        if (creationFailure) return;

        if (file == null) {
            LibraryPlugin.getInstance().getLibraryLogger().toConsole("CommentedConfiguration", "File cannot be null when using syncWithConfig");
            return;
        }

        if (resource == null) {
            LibraryPlugin.getInstance().getLibraryLogger().toConsole("CommentedConfiguration", "Input stream cannot be null when using syncWithConfig");
            return;
        }

        CommentedConfiguration cfg = loadConfiguration(resource);
        if (syncConfigurationSection(cfg, cfg.getConfigurationSection(""), Arrays.asList(ignoredSections))) {
            save(file);
        }
    }

    /**
     * Set a new comment to a path.
     * You can remove comments by providing a null as a comment argument.
     * @param path The path to set the comment to.
     * @param comment The comment to set. Supports multiple lines (use \n as a spacer).
     */
    public void setComment(String path, String comment) {
        if (comment == null) {
            configComments.remove(path);
        } else {
            configComments.put(path, comment);
        }
    }

    /**
     * Get a comment of a path.
     * @param path The path to get the comment of.
     * @return Returns a string that contains the comment. If no comment exists, null will be returned.
     */
    public String getComment(String path) {
        return getComment(path, null);
    }

    /**
     * Get a comment of a path with a default value if no comment exists.
     * @param path The path to get the comment of.
     * @param def A default comment that will be returned if no comment exists for the path.
     * @return Returns a string that contains the comment. If no comment exists, the def value will be returned.
     */
    public String getComment(String path, String def) {
        return configComments.getOrDefault(path, def);
    }

    /**
     * Checks whether a path has a comment or not.
     * @param path The path to check.
     * @return Returns true if there's an existing comment, otherwise false.
     */
    public boolean containsComment(String path) {
        return getComment(path) != null;
    }

    /**
     * Check if the config has failed to load.
     */
    public boolean hasFailed() {
        return creationFailure;
    }

    /**
     * Load all data related to the config file - keys, values and comments.
     * @param contents The contents of the file.
     * @throws InvalidConfigurationException if the contents are invalid.
     */
    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        super.loadFromString(contents);

        String[] lines = contents.split("\n");
        StringBuilder comments = new StringBuilder();
        String currentSection = "";

        for (String line : lines) {
            if (isComment(line)) {
                comments.append(line).append("\n");
            } else if (isNewSection(line)) {
                currentSection = getSectionPath(this, line, currentSection);
                if (comments.length() > 1) {
                    setComment(currentSection, comments.toString().substring(0, comments.length() - 1));
                }
                comments = new StringBuilder();
            }
        }
    }

    /**
     * Parsing all the data (keys, values and comments) into a valid string, that will be written into a file later.
     * @return A string that contains all the data, ready to be written into a file.
     */
    @Override
    public String saveToString() {
        this.options().header(null);

        List<String> lines = new ArrayList<>(Arrays.asList(super.saveToString().split("\n")));
        String currentSection = "";

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (isNewSection(line)) {
                currentSection = getSectionPath(this, line, currentSection);
                if (containsComment(currentSection)) {
                    lines.add(i, getComment(currentSection));
                    i++;
                }
            }
        }

        StringBuilder contents = new StringBuilder();
        for (String line : lines) {
            contents.append("\n").append(line);
        }

        return contents.length() == 0 ? "" : contents.substring(1);
    }

    /**
     * Sync a specific configuration section with another one, recursively.
     * @param commentedConfig The config that contains the data we need to sync with.
     * @param section The current section that we sync.
     * @param ignoredSections A list of ignored sections that won't be synced (unless not found in the file).
     * @return Returns true if there were any changes, otherwise false.
     */
    private boolean syncConfigurationSection(CommentedConfiguration commentedConfig, ConfigurationSection section, List<String> ignoredSections) {
        boolean changed = false;

        for (String key : section.getKeys(false)) {
            String path = section.getCurrentPath().isEmpty() ? key : section.getCurrentPath() + "." + key;
            boolean isIgnored = ignoredSections.stream().anyMatch(path::contains);

            if (section.isConfigurationSection(key)) {
                boolean containsSection = contains(path);
                if (!containsSection || !isIgnored) {
                    changed = syncConfigurationSection(commentedConfig, section.getConfigurationSection(key), ignoredSections) || changed;
                }
            } else if (!contains(path)) {
                if (shouldSyncIgnoredPath(isIgnored, path)) {
                    set(path, section.get(key));
                    changed = true;
                }
            }

            if (commentedConfig.containsComment(path) && !commentedConfig.getComment(path).equals(getComment(path))) {
                if (shouldSyncIgnoredPath(isIgnored, path)) {
                    setComment(path, commentedConfig.getComment(path));
                    changed = true;
                }
            }
        }

        if (changed) {
            correctIndexes(section, getConfigurationSection(section.getCurrentPath()));
        }

        return changed;
    }

    private boolean shouldSyncIgnoredPath(boolean isIgnored, String path) {
        if (!isIgnored) {
            return true;
        }
        String parentPath = getParentPath(path);
        return parentPath.isEmpty() || !contains(parentPath);
    }

    private CommentedConfiguration flagAsFailed() {
        creationFailure = true;
        return this;
    }

    /**
     * Load a config from a file.
     * @param file The file to load the config from.
     * @return A new instance of CommentedConfiguration contains all the data (keys, values and comments).
     */
    public static CommentedConfiguration loadConfiguration(@NonNull File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            return loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (FileNotFoundException ex) {
            Bukkit.getLogger().warning("File " + file.getName() + " doesn't exist.");
            return new CommentedConfiguration().flagAsFailed();
        }
    }

    /**
     * Load a config from an input-stream, which is used for resources that are obtained using JavaPlugin#getResource.
     * @param inputStream The input-stream to load the config from.
     * @return A new instance of CommentedConfiguration contains all the data (keys, values and comments).
     */
    public static CommentedConfiguration loadConfiguration(InputStream inputStream) {
        if (inputStream == null) {
            LibraryPlugin.getInstance().getLibraryLogger().toConsole("CommentedConfiguration", "InputStream cannot be null!");
            return new CommentedConfiguration().flagAsFailed();
        }
        return loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    /**
     * Load a config from a reader (used for files and streams together).
     * @param reader The reader to load the config from.
     * @return A new instance of CommentedConfiguration contains all the data (keys, values and comments).
     */
    public static CommentedConfiguration loadConfiguration(Reader reader) {
        CommentedConfiguration config = new CommentedConfiguration();

        try (BufferedReader bufferedReader = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
            StringBuilder contents = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                contents.append(line).append('\n');
            }
            config.loadFromString(contents.toString());
        } catch (IOException | InvalidConfigurationException ex) {
            config.flagAsFailed();
            ex.printStackTrace();
        }

        return config;
    }

    /**
     * Checks if a line is a new section or not.
     * Sadly, that's not possible to use ":" as a spacer between key and value, and ": " must be used.
     * @param line The line to check.
     * @return True if the line is a new section, otherwise false.
     */
    private static boolean isNewSection(String line) {
        String trimLine = line.trim();
        return trimLine.contains(": ") || trimLine.endsWith(":");
    }

    /**
     * Creates a full path of a line.
     * @param commentedConfig The config to get the path from.
     * @param line The line to get the path of.
     * @param currentSection The last known section.
     * @return The full path of the line.
     */
    private static String getSectionPath(CommentedConfiguration commentedConfig, String line, String currentSection) {
        String newSection = line.trim().split(": ")[0];

        if (newSection.endsWith(":")) {
            newSection = newSection.substring(0, newSection.length() - 1);
        }
        if (newSection.startsWith(".")) {
            newSection = newSection.substring(1);
        }
        if (newSection.startsWith("'") && newSection.endsWith("'")) {
            newSection = newSection.substring(1, newSection.length() - 1);
        }

        if (!currentSection.isEmpty() && commentedConfig.contains(currentSection + "." + newSection)) {
            newSection = currentSection + "." + newSection;
        } else {
            String parentSection = currentSection;
            while (!parentSection.isEmpty() && !commentedConfig.contains((parentSection = getParentPath(parentSection)) + "." + newSection)) {
            }
            newSection = parentSection.trim().isEmpty() ? newSection : parentSection + "." + newSection;
        }

        return newSection;
    }

    /**
     * Checks if a line represents a comment or not.
     * @param line The line to check.
     * @return True if the line is a comment (stars with # or it's empty), otherwise false.
     */
    private static boolean isComment(String line) {
        String trimLine = line.trim();
        return trimLine.startsWith("#") || trimLine.isEmpty();
    }

    /**
     * Get the parent path of the provided path, by removing the last '.' from the path.
     * @param path The path to check.
     * @return The parent path of the provided path.
     */
    private static String getParentPath(String path) {
        return path.contains(".") ? path.substring(0, path.lastIndexOf('.')) : "";
    }

    /**
     * Convert key-indexes of a section into the same key-indexes that another section has.
     * @param section The section that will be used as a way to get the correct indexes.
     * @param target The target section that will be ordered again.
     */
    private static void correctIndexes(ConfigurationSection section, ConfigurationSection target) {
        List<Pair<String, Object>> sectionMap = getSectionMap(section);
        List<Pair<String, Object>> correctOrder = new ArrayList<>();

        for (Pair<String, Object> entry : sectionMap) {
            correctOrder.add(new Pair<>(entry.getKey(), target.get(entry.getKey())));
        }

        clearConfiguration(target);
        for (Pair<String, Object> entry : correctOrder) {
            target.set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Parsing a section into a list that contains all of it's keys and their values without changing their order.
     * @param section The section to convert.
     * @return A list that contains all the keys and their values.
     */
    private static List<Pair<String, Object>> getSectionMap(ConfigurationSection section) {
        List<Pair<String, Object>> list = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            list.add(new Pair<>(key, section.get(key)));
        }
        return list;
    }

    /**
     * Clear a configuration section from all of its keys.
     * This can be done by setting all the keys' values to null.
     * @param section The section to clear.
     */
    private static void clearConfiguration(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            section.set(key, null);
        }
    }
}
