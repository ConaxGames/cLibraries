package com.conaxgames.libraries.board;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BoardAdapter {

	List<String> getScoreboard(Player player, Board board);

	String getTitle(Player player);

	long getInterval();

	void onScoreboardCreate(Player player, Scoreboard board);

	void preLoop();
	
	/**
	 * Async version of getScoreboard for better performance
	 * Default implementation calls the synchronous version
	 */
	default CompletableFuture<List<String>> getScoreboardAsync(Player player, Board board) {
		return CompletableFuture.completedFuture(getScoreboard(player, board));
	}
	
	/**
	 * Async version of getTitle for better performance
	 * Default implementation calls the synchronous version
	 */
	default CompletableFuture<String> getTitleAsync(Player player) {
		return CompletableFuture.completedFuture(getTitle(player));
	}

}