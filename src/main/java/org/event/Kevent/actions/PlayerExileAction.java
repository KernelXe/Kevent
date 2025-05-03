package org.event.Kevent.actions;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.audience.Audience;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerExileAction implements Listener {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<UUID, AtomicBoolean> isDying = new ConcurrentHashMap<>();


    private final ConcurrentHashMap<UUID, AtomicBoolean> hasShownMessage = new ConcurrentHashMap<>();

    public PlayerExileAction() {
        try {
            // ลงทะเบียน listener ใน constructor
            Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("Kevent"));
        } catch (Exception e) {
            Bukkit.getLogger().severe("ไม่สามารถลงทะเบียน PlayerExileAction listener: " + e.getMessage());
        }
    }

    public void exilePlayer(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId) && currentTime - cooldowns.get(playerId) < 5000) {
            player.sendMessage("§cโปรดรอ 5 วินาที ก่อนที่จะถูกคัดออกอีกครั้ง");
            return;
        }

        cooldowns.put(playerId, currentTime);

        AtomicBoolean dying = isDying.computeIfAbsent(playerId, k -> new AtomicBoolean(false));

        // ถ้ากำลังจะตายอยู่แล้ว ไม่ต้องทำอะไร
        if (dying.get()) {
            return;
        }

        AtomicBoolean shown = hasShownMessage.computeIfAbsent(playerId, k -> new AtomicBoolean(false));

        dying.set(true);

        player.damage(9999);

        Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("Kevent"),
                () -> dying.set(false),
                100L // 5 วินาที (100 ticks)
        );
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();

        AtomicBoolean dying = isDying.get(playerId);
        if (dying != null && dying.get()) {
            AtomicBoolean shown = hasShownMessage.computeIfAbsent(playerId, k -> new AtomicBoolean(false));

            if (!shown.getAndSet(true)) {
                String titleMessage = "<green>คุณ <yellow>" + player.getName() + " <red>ถูกคัดออก!";
                Component titleComponent = miniMessage.deserialize(titleMessage);
                Component subtitle = Component.text("");
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(1000));
                Title title = Title.title(titleComponent, subtitle, times);

                Audience audience = (Audience) player;
                audience.showTitle(title);

                String exileMessage = "ผู้เล่น " + player.getName() + " ถูกกำจัด!";
                Bukkit.broadcastMessage("§c" + exileMessage);
            }


            dying.set(false);
        }
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();


        hasShownMessage.remove(playerId);
        isDying.remove(playerId);
    }
}