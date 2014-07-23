package net.galaxygaming.tntrun.listener;

import net.galaxygaming.dispenser.game.GameFixedMetadata;
import net.galaxygaming.dispenser.game.GameState;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.tntrun.TNTRun;

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
            final Block block = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
            
            if (!block.getType().equals(Material.AIR)) {
                new GameRunnable() {
                    @Override
                    public void run() {
                        block.setType(Material.GLASS);
                    }
                }.runTaskLater(15L); // 0.75 seconds
                new GameRunnable() {
                		@Override
                		public void run() {
                			block.setType(Material.AIR);
                		}
                }.runTaskLater(30L); // 1.5 seconds
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event, final TNTRun game) {
    		Player entity = event.getEntity();
    		entity.setMetadata("spectator", new GameFixedMetadata(game, true));
    		entity.setGameMode(GameMode.CREATIVE);
    		Player alive = null;
    		for (Player player : game.getPlayers()) {
    			if (game.getMetadata(player, "spectator").asBoolean())
    				continue;
    			if (alive == null) {
    				alive = player;
    				continue;
    			}
    			game.setWinner(alive.getDisplayName());
    			game.end();
    		}
    }
}