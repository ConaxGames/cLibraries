package com.conaxgames.libraries.timer.impl;

import com.conaxgames.libraries.message.TimeUtil;
import com.conaxgames.libraries.timer.PlayerTimer;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EnderpearlTimer extends PlayerTimer implements Listener {

	public EnderpearlTimer() {
		super("Enderpearl", TimeUnit.SECONDS.toMillis(15));
	}

	@Override
	protected void handleExpiry(Player player, UUID playerUUID) {
		super.handleExpiry(player, playerUUID);

		if (player == null) {
			return;
		}

		player.sendMessage(CC.GREEN + "You're now able to use enderpearls!");
		//player.sendMessage(CC.PRIMARY + "Your pearl cooldown has expired!");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
				|| !event.hasItem()) {
			return;
		}
		Player player = event.getPlayer();

		if (event.getItem().getType() == XMaterial.ENDER_PEARL.get()) {
			long cooldown = this.getRemaining(player);
			if (cooldown > 0) {
				event.setCancelled(true);
				player.sendMessage(CC.RED + "You must wait another " + TimeUtil.getTimerRemaining(cooldown, true, false) + " before throwing another ender pearl.");
				player.updateInventory();
			}
		}
	}

	@EventHandler
	public void onPearlLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			if (event.getEntity() instanceof EnderPearl) {
				Player player = (Player) event.getEntity().getShooter();
				this.setCooldown(player, player.getUniqueId());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			return;
		}
		Player player = event.getPlayer();

		if (this.getRemaining(player) != 0) {
			// Was the event cancelled?
			if (event.isCancelled()) {
				this.clearCooldown(player);
			}
		}

		event.getTo().setX((double) event.getTo().getBlockX() + 0.5D);
		event.getTo().setZ((double) event.getTo().getBlockZ() + 0.5D);
		if (event.getTo().getBlock().getType() != XMaterial.AIR.get()) {
			event.getTo().setY(event.getTo().getY() - (event.getTo().getY() - event.getTo().getBlockY()));
		}
	}
}
