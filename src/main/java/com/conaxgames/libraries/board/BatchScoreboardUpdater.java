package com.conaxgames.libraries.board;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Batch scoreboard updater to reduce network packets and improve performance
 */
public class BatchScoreboardUpdater {
    
    private final Map<UUID, List<ScoreboardUpdate>> pendingUpdates = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastFlushTime = new ConcurrentHashMap<>();
    private static final long FLUSH_INTERVAL = 50; // 50ms flush interval
    
    public static class ScoreboardUpdate {
        private final String key;
        private final String prefix;
        private final String suffix;
        private final int position;
        
        public ScoreboardUpdate(String key, String prefix, String suffix, int position) {
            this.key = key;
            this.prefix = prefix;
            this.suffix = suffix;
            this.position = position;
        }
        
        public String getKey() { return key; }
        public String getPrefix() { return prefix; }
        public String getSuffix() { return suffix; }
        public int getPosition() { return position; }
    }
    
    public void addUpdate(UUID playerId, ScoreboardUpdate update) {
        pendingUpdates.computeIfAbsent(playerId, k -> new ArrayList<>()).add(update);
    }
    
    public void flushUpdates() {
        long now = System.currentTimeMillis();
        
        pendingUpdates.entrySet().removeIf(entry -> {
            UUID playerId = entry.getKey();
            List<ScoreboardUpdate> updates = entry.getValue();
            
            // Check if it's time to flush for this player
            Long lastFlush = lastFlushTime.get(playerId);
            if (lastFlush == null || (now - lastFlush) >= FLUSH_INTERVAL) {
                // Send batched updates
                sendBatchedUpdates(playerId, updates);
                lastFlushTime.put(playerId, now);
                return true; // Remove from pending
            }
            return false; // Keep in pending
        });
    }
    
    private void sendBatchedUpdates(UUID playerId, List<ScoreboardUpdate> updates) {
        Player player = org.bukkit.Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            return;
        }
        
        // Group updates by team to minimize API calls
        Map<String, List<ScoreboardUpdate>> teamUpdates = new ConcurrentHashMap<>();
        for (ScoreboardUpdate update : updates) {
            teamUpdates.computeIfAbsent(update.getKey(), k -> new ArrayList<>()).add(update);
        }
        
        // Apply batched updates
        for (Map.Entry<String, List<ScoreboardUpdate>> teamEntry : teamUpdates.entrySet()) {
            String teamName = teamEntry.getKey();
            List<ScoreboardUpdate> teamUpdateList = teamEntry.getValue();
            
            if (!teamUpdateList.isEmpty()) {
                ScoreboardUpdate lastUpdate = teamUpdateList.get(teamUpdateList.size() - 1);
                applyTeamUpdate(player, teamName, lastUpdate);
            }
        }
    }
    
    private void applyTeamUpdate(Player player, String teamName, ScoreboardUpdate update) {
        try {
            Scoreboard scoreboard = player.getScoreboard();
            org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamName);
            
            if (team != null) {
                // Batch team updates
                if (!update.getPrefix().equals(team.getPrefix())) {
                    team.setPrefix(update.getPrefix());
                }
                if (!update.getSuffix().equals(team.getSuffix())) {
                    team.setSuffix(update.getSuffix());
                }
                
                // Update score
                org.bukkit.scoreboard.Objective objective = scoreboard.getObjective("Default");
                if (objective != null) {
                    org.bukkit.scoreboard.Score score = objective.getScore(update.getKey());
                    score.setScore(update.getPosition());
                }
            }
        } catch (Exception e) {
            // Handle any exceptions during batch update
            e.printStackTrace();
        }
    }
    
    public void clearPlayerUpdates(UUID playerId) {
        pendingUpdates.remove(playerId);
        lastFlushTime.remove(playerId);
    }
} 