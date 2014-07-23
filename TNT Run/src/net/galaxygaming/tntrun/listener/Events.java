package net.galaxygaming.tntrun.listener;

import net.galaxygaming.dispenser.game.GameState;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.tntrun.TNTRun;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class Events implements Listener {
	
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event, final TNTRun game) {
        if (game.getState().ordinal() > GameState.ACTIVE.ordinal()) {
            final Block block = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
            
            if (!block.getType().equals(Material.AIR)) {
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