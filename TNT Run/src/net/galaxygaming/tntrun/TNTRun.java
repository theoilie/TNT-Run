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

// All games have to extend GameBase or types of games that extend GameBase, such as TeamGame.
public class TNTRun extends GameBase {
	
	// We use the @Component annotation to tell GameDispenser to automatigically track
	// this variable. It also sets up the command /game set <map> <component>, with
	// component being replaced by the variable name.
	private @Component Location spawn;
	
	// RegenableSelections can be regenerated at any time with RegenableSelection#regen().
	private @Component RegenableSelection arena;
	
	// Variable to store the winner.
	private String winner;
	// The team to add everyone who loses to.
	private Spectator spectatorTeam;
	// Just a map to store how long it has been since a player last moved (to prevent being AFK).
	private Map<Player, Integer> times;

	// Gets the spawn location so that other classes can know it as well.
	public Location getSpawn() {
		return spawn;
	}
	
	// Gets the arena variable so that other classes can know it as well.
	public RegenableSelection getArena() {
		return arena;
	}

	// Called when the game first loads.
	@Override
	public void onLoad() {
		useScoreboardPlayers = true;
		playerTagScore = 2;
		playerCounterScore = 1;
	}

	// Called when the game starts.
	// You will need to reset all of your variables here so that
	// they do not carry over from previous games.
	@Override
	public void onStart() {
		spectatorTeam = new Spectator();
		times = Maps.newHashMap();
		for (Player player : getPlayers()) {
			player.teleport(spawn);
			player.setScoreboard(board);
			if (player.getGameMode() != GameMode.SURVIVAL)
				player.setGameMode(GameMode.SURVIVAL);
		}
	}

	// Called every second.
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

	// Called when the game ends.
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

	// Updates the scoreboard.
	@Override
	public void updatePlayerBoard() {
		if (playerTagScore > 0) {
			Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&6&lPlayers"));
			if (score.getScore() != playerTagScore)
				score.setScore(playerTagScore);
		}

		if (playerCounterScore > 0) {
			board.resetScores(lastPlayerCount + "");
			lastPlayerCount = getPlayers().length - spectatorTeam.getSize();
			objective.getScore(lastPlayerCount + "").setScore(playerCounterScore);
		}
	}

	// Gets the winner variable so that other classes can see it, too.
	public String getWinner() {
		return winner;
	}

	// Sets the winner variable.
	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	// Gets the spectator team that losers or players who join late are added to.
	public Spectator getSpectatorTeam() {
		return spectatorTeam;
	}

	// Called whenever a player leaves the game, either with /game leave,
	// quitting the server, or getting kicked from the server.
	@Override
	public void onPlayerLeave(Player player) {
		spectatorTeam.remove(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	// Gets the last time since a player has moved (to prevent AFKing).
	public int getTime(Player player) {
		if (times.containsKey(player))
			return times.get(player);
		else {
			times.put(player, 0);
			return 0;
		}
	}
	
	// Resets a player's time. This should be called every time a player moves.
	public void resetTime(Player player) {
		if (times.containsKey(player))
			times.remove(player);
	}
	
	// Checks a player's time. Gives a warning if he/she needs to keep moving, or
	// makes the player lose if he/she has been standing for too long.
	private void checkTime(Player player) {
		int time = getTime(player);
		time++;
		if (time == 2) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', getType().getMessages().getMessage("game.warning")));
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
