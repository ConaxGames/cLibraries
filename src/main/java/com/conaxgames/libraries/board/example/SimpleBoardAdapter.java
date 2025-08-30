package com.conaxgames.libraries.board.example;

import com.conaxgames.libraries.board.Board;
import com.conaxgames.libraries.board.BoardAdapter;
import com.conaxgames.libraries.board.BoardTimer;
import com.conaxgames.libraries.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple example implementation of BoardAdapter.
 * This demonstrates how to create a basic scoreboard with dynamic content.
 * 
 * @author ConaxGames
 * @since 1.0
 */
public class SimpleBoardAdapter implements BoardAdapter {

    @Override
    public List<String> getScoreboard(Player player, Board board) {
        List<String> lines = new ArrayList<>();
        
        // Add some basic information
        lines.add(CC.translate("&7&m" + "─".repeat(20)));
        lines.add(CC.translate("&b&lPlayer Info"));
        lines.add(CC.translate("&7Name: &f" + player.getName()));
        lines.add(CC.translate("&7Health: &c" + (int) player.getHealth() + "&7/&c" + (int) player.getMaxHealth()));
        lines.add(CC.translate("&7Food: &6" + player.getFoodLevel() + "&7/&620"));
        lines.add(CC.translate("&7XP: &a" + player.getLevel()));
        lines.add("");
        
        // Add timer example
        BoardTimer timer = board.getTimer("example");
        if (timer != null && !timer.isExpired()) {
            lines.add(CC.translate("&7Timer: &e" + timer.getFormattedString(BoardTimer.TimerType.SECONDS)));
        }
        
        lines.add(CC.translate("&7&m" + "─".repeat(20)));
        
        return lines;
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("&b&l" + player.getName() + "'s Scoreboard");
    }

    @Override
    public long getInterval() {
        return 20L; // Update every second
    }

    @Override
    public void onScoreboardCreate(Player player, Scoreboard board) {
        // Optional: Add a timer when the scoreboard is created
        BoardTimer timer = new BoardTimer("example", 60.0); // 60 second timer
        // Note: You would need to add this timer to the board manually if needed
    }

    @Override
    public void preLoop() {
        // Optional: Called before updating all boards
        // You can use this for global updates or optimizations
    }
}
