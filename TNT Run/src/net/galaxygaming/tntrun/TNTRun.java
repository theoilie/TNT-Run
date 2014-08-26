package net.galaxygaming.tntrun;

import net.galaxygaming.dispenser.game.GameBase;
import net.galaxygaming.dispenser.game.component.Component;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.dispenser.team.Spectator;
import net.galaxygaming.selection.RegenableSelection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

public class TNTRun extends GameBase {
	private @Component Location spawn;
	private @Component RegenableSelection arena;
	private String winner;
	private Spectator spectatorTeam;

	public Location getSpawn() {
		return spawn;
	}
	
	public RegenableSelection getArena() {
		return arena;
	}

	@Override
	public void onLoad() {				
		useScoreboardPlayers = true;
	}

	@Override
	public void onStart() {
		spectatorTeam = new Spectator();
		for (Player player : getPlayers()) {
			player.teleport(spawn);
			player.setScoreboard(board);
		}
	}

	@Override
	public void onTick() {
		
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
	}
}