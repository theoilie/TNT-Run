package net.galaxygaming.tntrun;

import java.util.Map;

import net.galaxygaming.dispenser.game.GameBase;
import net.galaxygaming.dispenser.game.component.Component;
import net.galaxygaming.dispenser.game.GameState;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.dispenser.team.Spectator;
import net.galaxygaming.selection.RegenableSelection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import com.google.common.collect.Maps;

public class TNTRun extends GameBase {
	private @Component Location spawn;
	private @Component RegenableSelection arena;
	private String winner;
	private Spectator spectatorTeam;
	private Map<Player, Integer> times = Maps.newHashMap();

	public Location getSpawn() {
		return spawn;
	}
	
	public RegenableSelection getArena() {
		return arena;
	}

	@Override
	public void onLoad() {					
		spectatorTeam = new Spectator();
		useScoreboardPlayers = true;
		playerTagScore = 2;
		playerCounterScore = 1;
	}

	@Override
	public void onStart() {		
		for (Player player : getPlayers()) {
			player.teleport(spawn);
			player.setScoreboard(board);
			if (player.getGameMode() != GameMode.SURVIVAL)
				player.setGameMode(GameMode.SURVIVAL);
		}
	}

	@Override
	public void onSecond() {
		if (getState().ordinal() != GameState.ACTIVE.ordinal())
			return;
		
		for (Player player : getPlayers()) {
			if (spectatorTeam.isOnTeam(player))
				continue;
			checkTime(player);
		}
	}

	@Override
	public void onEnd() {
		broadcast("&6" + winner + " &rhas won the game!");
		new GameRunnable() {
			@Override
			public void run() {
				arena.regen();
			}
		}.runTask();
		
		for (Player player : getPlayers()) {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}

	@Override
	public void updatePlayerBoard() {
		if (playerTagScore > 0) {
			Score score = objective.getScore(ChatColor
					.translateAlternateColorCodes('&', "&6&lPlayers"));
			if (score.getScore() != playerTagScore)
				score.setScore(playerTagScore);
		}

		if (playerCounterScore > 0) {
			board.resetScores(lastPlayerCount + "");
			lastPlayerCount = getPlayers().length - spectatorTeam.getSize();
			objective.getScore(lastPlayerCount + "").setScore(
					playerCounterScore);
		}
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	public Spectator getSpectatorTeam() {
		return spectatorTeam;
	}

	@Override
	public void onPlayerLeave(Player player) {
		spectatorTeam.remove(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	public int getTime(Player player) {
		if (times.containsKey(player))
			return times.get(player);
		else {
			times.put(player, 0);
			return 0;
		}
	}
	
	public void resetTime(Player player) {
		if (times.containsKey(player))
			times.remove(player);
	}
	
	private void checkTime(Player player) {
		int time = getTime(player);
		time++;
		if (time == 2) {
			player.sendMessage(ChatColor.translateAlternateColorCodes
				('&', getType().getMessages().getMessage("game.warning")));
			resetTime(player);
			times.put(player, time);
		} else if (time == 5) {
			player.setHealth(0);
			resetTime(player);
		} else {
			resetTime(player);
			times.put(player, time);
		}
	}
}