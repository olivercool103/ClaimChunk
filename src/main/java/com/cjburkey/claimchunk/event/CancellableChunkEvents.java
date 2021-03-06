package com.cjburkey.claimchunk.event;

import com.cjburkey.claimchunk.ChunkHelper;
import com.cjburkey.claimchunk.ClaimChunk;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CancellableChunkEvents implements Listener {
	
	// Block Break
	@EventHandler
	public void onBlockBroken(BlockBreakEvent e) {
		if (e != null && e.getPlayer() != null && e.getBlock() != null) {
			ChunkHelper.cancelEventIfNotOwned(e.getPlayer(), e.getBlock().getChunk(), e);
		}
	}
	
	// Clicking on Blocks
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e != null && e.getPlayer() != null && e.getClickedBlock() != null) {
			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			if (e.getAction() == Action.LEFT_CLICK_AIR) {
				return;
			}
			if (e.getAction() == Action.RIGHT_CLICK_AIR) {
				return;
			}
			ChunkHelper.cancelEventIfNotOwned(e.getPlayer(), e.getClickedBlock().getChunk(), e);
		}
	}
	
	// Item Frame Rotation
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		if (e != null && e.getPlayer() != null && e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
			ChunkHelper.cancelEventIfNotOwned(e.getPlayer(), e.getRightClicked().getLocation().getChunk(), e);
		}
	}
	
	// Item Frame Break
	@EventHandler
	public void onItemFrameBroken(HangingBreakByEntityEvent e) {
		if (e != null && e.getEntity().getType().equals(EntityType.ITEM_FRAME) && e.getRemover().getType().equals(EntityType.PLAYER)) {
			ChunkHelper.cancelEventIfNotOwned((Player) e.getRemover(), e.getEntity().getLocation().getChunk(), e);
		}
	}
	
	// Item Frame Place
	@EventHandler
	public void onItemFramePlaced(HangingPlaceEvent e) {
		if (e != null && e.getEntity().getType().equals(EntityType.ITEM_FRAME) && e.getPlayer() != null) {
			ChunkHelper.cancelEventIfNotOwned(e.getPlayer(), e.getEntity().getLocation().getChunk(), e);
		}
	}
	
	// Item Frame Remove Item
	@EventHandler
	public void onItemFramePlaced(EntityDamageByEntityEvent e) {
		if (e != null && e.getEntity().getType().equals(EntityType.ITEM_FRAME) && e.getDamager().getType().equals(EntityType.PLAYER)) {
			ChunkHelper.cancelEventIfNotOwned((Player) e.getDamager(), e.getEntity().getLocation().getChunk(), e);
		}
	}

    // TnT and Creeper explosions
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!e.isCancelled()) {
            if (!ClaimChunk.getInstance().getChunkHandler().isClaimed(e.getLocation().getChunk())) {
                return;
            }
            ChunkHelper.cancelExplosionIfConfig(e);
        }
    }

    // Animal damage
    @EventHandler()
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!ClaimChunk.getInstance().getChunkHandler().isClaimed(e.getEntity().getLocation().getChunk())) {
            return;
        }
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Animals)
            ChunkHelper.cancelAnimalDamage((Player) e.getDamager(), e.getDamager().getLocation().getChunk(), e);
    }
}