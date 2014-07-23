package net.galaxygaming.tntrun.listener;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import net.galaxygaming.tntrun.TNTRun;

import net.galaxygaming.gamedispenser.game.GameState;
import net.galaxygaming.gamedispenser.task.GameRunnable;

public class Events implements Listener {
	
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event, final TNTRun game) {
        if (game.getState().ordinal() > GameState.ACTIVE.ordinal()) {
            final Block block = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
            
            if (!block.getMaterial().equals(Material.AIR)) {
                new GameRunnable() {
                    @Override
                    public void run() {
                        block.setType(Material.AIR);
                    }
                }.runTaskLater(60L); // 3 seconds
            }
        }
    }
}