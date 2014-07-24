package net.galaxygaming.tntrun;

import net.galaxygaming.dispenser.game.GameBase;
import net.galaxygaming.dispenser.team.Spectator;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.LocationUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class TNTRun extends GameBase {
	private Location spawn;
	private Selection arena;
	private String winner;
	private String joinMessage, remainingMessage;
	private Spectator spectatorTeam;
	
	private void setSpawn(Location spawn) {
		this.spawn = spawn;
		getConfig().set("spawn", LocationUtil.serializeLocation(spawn));
		save();
	}
	
	private void setArena(Selection arena) {
		this.arena = arena;
		getConfig().set("arena", arena);
		save();
	}

	public Location getSpawn() {
		return spawn;
	}
	
	public Selection getArena() {
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
		arena = (Selection) getConfig().get("arena");
		
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
		
		int length = getPlayers().length;
		getConfig().addDefault("remaining message", 
			"(&6" + (length - spectatorTeam.getSize()) 
			+ "&r/&6" + length + "&r)");
		getConfig().addDefault(
				"join message", "(&6" + getPlayers().length
					+ "&r/&6" + getConfig().getInt("maximum players")
					+ "&r)");
		save();
		joinMessage = getConfig().getString("join message");
		remainingMessage = getConfig().getString("remaining message");
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
	
	public String getRemainingMessage() {
		return remainingMessage;
	}
	
	public String getJoinMessage() {
		return joinMessage;
	}

	@Override
	public void onPlayerJoin(Player player) {
		broadcast("&6" + player.getDisplayName() 
			+ "&rhas joined." + joinMessage);
	}

	@Override
	public void onPlayerLeave(Player player) {
		broadcast("&6" + player.getDisplayName() 
			+ "&rhas joined." + joinMessage);
	}
}