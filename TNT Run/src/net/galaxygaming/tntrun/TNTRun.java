package net.galaxygaming.tntrun;

import net.galaxygaming.dispenser.game.GameBase;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.dispenser.team.Spectator;
import net.galaxygaming.selection.RegenableSelection;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.LocationUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class TNTRun extends GameBase {
	private Location spawn;
	private RegenableSelection arena;
	private String winner;
	private Spectator spectatorTeam;
	
	private void setSpawn(Location spawn) {
		this.spawn = spawn;
		getConfig().set("spawn", LocationUtil.serializeLocation(spawn));
		save();
	}
	
	private void setArena(Selection arena) {
		this.arena = new RegenableSelection(this, "arena", arena);
	}

	public Location getSpawn() {
		return spawn;
	}
	
	public RegenableSelection getArena() {
		return arena;
	}
	
	@Override
	public boolean setComponent(String componentName, Location location) {
		if (componentName.equalsIgnoreCase("spawn")) {
			setSpawn(location);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setComponent(String componentName, Selection selection) {
		if (componentName.equalsIgnoreCase("arena")) {
			setArena(selection);
			return true;
		}
		return false;
	}

	@Override
	public void onLoad() {
		spawn = LocationUtil.deserializeLocation(getConfig().getString("spawn"));
		arena = RegenableSelection.load(this, "arena");
		
		addComponent("arena");
		addComponent("spawn");
		
		spectatorTeam = new Spectator();
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = board.registerNewObjective
			(ChatColor.translateAlternateColorCodes('&', "&4TNT &cRun"), "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.getScore(ChatColor.translateAlternateColorCodes('&', "&6&lPlayers")).setScore(2);
		lastPlayerCount = getPlayers().length;
		objective.getScore(lastPlayerCount + "").setScore(1);
	}

	@Override
	public void onStart() {
		for (Player player : getPlayers()) {
			player.teleport(spawn);
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
	}

	@Override
	public boolean isSetup() {
		return spawn != null && arena != null;
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