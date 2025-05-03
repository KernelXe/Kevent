package org.event.Kevent.listeners;

import org.event.Kevent.Kevent;
import org.event.Kevent.utils.RegionChecker;
import org.event.Kevent.actions.PlayerExileAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerMovementListener implements Listener {

    private final Kevent plugin;

    public PlayerMovementListener(Kevent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("detection-enabled", true)) return;

        if (RegionChecker.isPlayerInRegion(player, config)) {
            PlayerExileAction exileAction = new PlayerExileAction();
            exileAction.exilePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("detection-enabled", true)) return;

        if (RegionChecker.isPlayerInRegion(player, config)) {
            PlayerExileAction exileAction = new PlayerExileAction();
            exileAction.exilePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("detection-enabled", true)) return;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (RegionChecker.isPlayerInRegion(player, config)) {
                PlayerExileAction exileAction = new PlayerExileAction();
                exileAction.exilePlayer(player);
            }
        }
    }
}
