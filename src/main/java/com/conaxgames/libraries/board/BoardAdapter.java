package com.conaxgames.libraries.board;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public interface BoardAdapter {

    List<String> getScoreboard(Player player, Board board);

    String getTitle(Player player);

    long getInterval();

    default void onScoreboardCreate(Player player, Scoreboard board) {
    }

    default void preLoop() {
    }
}
