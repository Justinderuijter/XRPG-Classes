package me.xepos.rpg;

import me.xepos.rpg.classes.XRPGClass;
import me.xepos.rpg.commands.ChangeClassCommand;
import me.xepos.rpg.commands.SmokeBombCommand;
import me.xepos.rpg.commands.XRPGDebug;
import me.xepos.rpg.commands.XRPGReload;
import me.xepos.rpg.configuration.*;
import me.xepos.rpg.database.DatabaseManagerFactory;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.datatypes.fireballData;
import me.xepos.rpg.dependencies.parties.IPartyManager;
import me.xepos.rpg.dependencies.parties.PartyManagerFactory;
import me.xepos.rpg.dependencies.protection.ProtectionSet;
import me.xepos.rpg.dependencies.protection.ProtectionSetFactory;
import me.xepos.rpg.listeners.*;
import me.xepos.rpg.tasks.ClearHashMapTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public final class XRPG extends JavaPlugin {

    private Inventory inventoryGUI;

    private static IPartyManager partyManager;
    private static IDatabaseManager databaseManager;

    private ProtectionSet protectionSet;

    public static HashMap<UUID, XRPGPlayer> RPGPlayers = new HashMap<>();
    public ConcurrentHashMap<Integer, fireballData> fireBalls = new ConcurrentHashMap<>();
    //Should this be concurrent

    @Override // Plugin startup logic
    @SuppressWarnings("")
    public void onEnable() {
        this.databaseManager = DatabaseManagerFactory.getDatabaseManager();
        this.partyManager = PartyManagerFactory.getPartyManager();
        this.protectionSet = ProtectionSetFactory.getProtectionRules();

        //Prevents throwing error if databaseManager shuts down this plugin.
        if(!this.isEnabled())
            return;
        //Loading/Creating configs
        loadConfigs();

        initClassChangeGUI();
        //registering listeners/commands
        initEventListeners();

        this.getCommand("smoke").setExecutor(new SmokeBombCommand());
        this.getCommand("xrpgdebug").setExecutor(new XRPGDebug(this));
        this.getCommand("xrpgreload").setExecutor(new XRPGReload());
        this.getCommand("changeclass").setExecutor(new ChangeClassCommand(this, inventoryGUI));
        System.out.println("RPG classes loaded!");

        int timer = this.getConfig().getInt("Garbage Collection.Timer", 120);
        if (timer > 0)
            new ClearHashMapTask(this, fireBalls).runTaskTimerAsynchronously(this, timer * 20L, timer * 20L);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.databaseManager.disconnect();
    }

    private void initClassChangeGUI()
    {
        inventoryGUI = Bukkit.createInventory(null, 18, "Pick A Class");
        inventoryGUI.setItem(17, buildItemStack(Material.BLAZE_ROD, "Wizard", new ArrayList<String>(){{
            add("A long range class with limited melee potential");
            add("Cast fire, wind and ice spells");
            add("Empower spells by maintaining fireball stacks");
            add("High damage, medium crowd control and low mobility");
        }}));

        inventoryGUI.setItem(16, buildItemStack(Material.DIAMOND_AXE, "Ravager", new ArrayList<String>(){{
            add("A melee class focused on dealing AoE damage");
            add("Attacking with an axe grants rage");
            add("Bonus effects scaling with rage level");
            add("High damage, medium mobility and low crowd control");
        }}));

        inventoryGUI.setItem(15, buildItemStack(Material.BOW, "Ranger", new ArrayList<String>(){{
            add("A ranged class focused using the bow");
            add("Switch between different arrow types");
            add("Add tipped arrows to the mix for even more power");
            add("High damage, medium mobility and low crowd control");
        }}));

        inventoryGUI.setItem(14, buildItemStack(Material.NETHERITE_HOE, "Necromancer", new ArrayList<String>(){{
            add("A melee class focused on applying status effects");
            add("Fallen opponents will join your army of the undead");
            add("Each with their own unique attributes");
            add("medium damage, medium mobilty, high crowd control");
        }}));

        inventoryGUI.setItem(13, buildItemStack(Material.SHIELD, "Guardian", new ArrayList<String>(){{
            add("A tanky melee class focused on survivability");
            add("Call upon Aegis' to protect you and your allies");
            add("Draw the attention of nearby mobs with your attacks");
            add("medium damage, low mobilty, high defense");
        }}));

        inventoryGUI.setItem(12, buildItemStack(Material.POPPY, "Brawler", new ArrayList<String>(){{
            add("A melee class that uses its fists as its weapon");
            add("Draw power from your armor");
            add("Choice between healing and extra damage");
            add("high damage, low mobilty, no crowd control");
        }}));

        inventoryGUI.setItem(11, buildItemStack(Material.ENCHANTED_GOLDEN_APPLE, "Bard", new ArrayList<String>(){{
            add("A support class focused on keeping your allies alive");
            add("Consume golden apples or potions to heal your allies");
            add("Grant yourself and an ally invulnerability");
            add("medium damage, low mobilty, high crowd control");
        }}));

        inventoryGUI.setItem(10, buildItemStack(Material.DIAMOND_SWORD, "Assassin", new ArrayList<String>(){{
            add("A melee class focused on striking its enemies from behind");
            add("Execute low health targets");
            add("Grant yourself stealth and get backstrikes");
            add("medium damage, high mobilty, low crowd control");
        }}));
    }

    public static XRPGPlayer setupRPGPlayer(UUID playerId, String playerClass)
    {
        try
        {
            Class<?> clazz = Class.forName("me.xepos.rpg.classes." + playerClass.replace("\"", ""));
            Constructor<?> constructor = clazz.getConstructor(XRPG.class);
            Object classInstance = constructor.newInstance(XRPG.getPlugin(XRPG.class));
            //Make the RPGPlayer
            return new XRPGPlayer(playerId, (XRPGClass) classInstance);
        }catch(ClassNotFoundException ex){
            ex.printStackTrace();
            System.out.println("Attempt to make an unknown class failed!");
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ItemStack buildItemStack(Material material, String displayName, List<String> lore)
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null){
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void initEventListeners() {
        getServer().getPluginManager().registerEvents(new ClassListener(this, databaseManager), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new XRPGListener(), this);
    }

    private void loadConfigs() {
        this.saveDefaultConfig();

        //Force class configuration to load
        AssassinConfig.getInstance();
        BardConfig.getInstance();
        BrawlerConfig.getInstance();
        GuardianConfig.getInstance();
        NecromancerConfig.getInstance();
        RangerConfig.getInstance();
        RavagerConfig.getInstance();
        WizardConfig.getInstance();
    }

    public ProtectionSet getProtectionSet() {
        return protectionSet;
    }

    public IPartyManager getPartyManager() {
        return partyManager;
    }
}