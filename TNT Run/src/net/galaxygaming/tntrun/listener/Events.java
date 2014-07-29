package net.galaxygaming.tntrun.listener;

import net.galaxygaming.dispenser.game.GameState;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.dispenser.team.Team;
import net.galaxygaming.tntrun.TNTRun;
import net.galaxygaming.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public class Events implements Listener {
	
    @EventHandler
	public void onPlayerMove(final PlayerMoveEvent event, final TNTRun game) {
		if (game.getState().ordinal() == GameState.ACTIVE.ordinal()) {
			final Block block = event.getPlayer().getLocation().getBlock()
					.getRelative(BlockFace.DOWN);
			if (block.getType() == Material.AIR || block.getType() == Material.GLASS)
				return;
			if (!game.getArena().getSelection().isIn(block.getLocation())) // No griefing outside of reset area
				return;
			
			new GameRunnable() {
				@Override
				public void run() {
					block.setType(Material.GLASS);
				}
			}.runTaskLater(5L); // 0.25 seconds
			new GameRunnable() {
				@Override
				public void run() {
					block.setType(Material.AIR);
				}
			}.runTaskLater(10L); // 0.5 seconds
		}
	}
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event, final TNTRun game) {
		Player entity = event.getEntity();
		Team spectatorTeam = game.getSpectatorTeam();
		spectatorTeam.add(entity);
		int length = game.getPlayers().length;
		game.broadcast("&4" + entity.getDisplayName() + " &ehas fallen out. "
						+ FormatUtil.format(game.getType().getMessages().getMessage("game.playerCount"), 
								game.getPlayers().length - spectatorTeam.getSize()));
		event.setDeathMessage("");
		event.getDrops().clear();
		entity.setGameMode(GameMode.CREATIVE);
		Player alive = null;
		for (Player player : game.getPlayers()) {
			if (spectatorTeam.isOnTeam(player))
				continue;
			if (alive == null) {
				alive = player;
				if (length > 2)
					continue;
			}
			game.setWinner(alive.getDisplayName());
			game.end();
		}
    }
}