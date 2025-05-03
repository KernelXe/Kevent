package org.event.Kevent;

import org.bukkit.plugin.java.JavaPlugin;
import org.event.Kevent.commands.*;
import org.event.Kevent.gui.UserGUIManager;
import org.event.Kevent.listeners.GUIListener;
import org.event.Kevent.listeners.PlayerMovementListener;
import org.event.Kevent.listeners.PlayerInteractListener;
import org.event.Kevent.actions.PlayerExileAction; // เพิ่มการใช้งาน PlayerExileAction
import org.event.Kevent.items.DetectionToggleItem; // เพิ่มการนำเข้าสำหรับ DetectionToggleItem
import org.event.Kevent.items.RegionStickItem; // เพิ่มการนำเข้าสำหรับ RegionStickItem

public class Kevent extends JavaPlugin {
    private static Kevent instance;
    private UserGUIManager guiManager;


    @Override
    public void onEnable() {
        // สร้างไฟล์ config ถ้ายังไม่มี
        saveDefaultConfig();
        instance = this;
        guiManager = new UserGUIManager(this);


        // สร้างอินสแตนซ์ของไอเทม
        DetectionToggleItem toggleItem = new DetectionToggleItem(this);
        RegionStickItem regionStick = new RegionStickItem(this);

        // Register command
        this.getCommand("usermanage").setExecutor(new UserManageCommand(this));
        this.getCommand("toggleDetection").setExecutor(new ToggleDetectionCommand(this));
        this.getCommand("removeRegion").setExecutor(new RemoveRegionCommand(this));
        this.getCommand("gettoggleitem").setExecutor(new GetToggleItemCommand(this, toggleItem));
        this.getCommand("regionstick").setExecutor(regionStick); // ลงทะเบียนคำสั่ง regionstick

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(guiManager), this);
        getServer().getPluginManager().registerEvents(regionStick, this); // ลงทะเบียน RegionStickItem เป็น event listener

        // แสดงข้อความเกี่ยวกับระบบสิทธิ์
        getLogger().info("กำลังใช้ระบบสิทธิ์แบบ permissions");
        getLogger().info("สิทธิ์ที่จำเป็น: kevent.regionstick");

        // เพิ่มการเรียกใช้งาน PlayerExileAction ที่นี่ถ้าต้องการใช้งาน
        // (ฟังก์ชันการคัดออกผู้เล่นจะถูกใช้งานเมื่อผู้เล่นขยับในพื้นที่ที่กำหนด)

        getLogger().info("Kevent Plugin ได้เริ่มทำงานแล้ว!");
    }

    public UserGUIManager getGuiManager() {
        return guiManager;
    }

    public static Kevent getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // บันทึก config ถ้ามีการเปลี่ยนแปลง
        saveConfig();
    }
}