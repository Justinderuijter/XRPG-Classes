package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassLoader {
    private final XRPG plugin;
    private static final File rpgFolder = Bukkit.getServer().getPluginManager().getPlugin("RPG").getDataFolder();
    private static final File classFolder = new File(rpgFolder, "Classes");

    public ClassLoader(XRPG plugin) {
        this.plugin = plugin;
    }

    public void checkClassFolder() {

        try {
            if (isDirEmpty(classFolder)) {
                //extract classes
            }
        } catch (IOException exception) {
            Bukkit.getLogger().info("Failed to read class folder!");
        }

    }

    private static boolean isDirEmpty(final File directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory.toPath())) {
            return !dirStream.iterator().hasNext();
        }
    }

    public void load(String classId, XRPGPlayer xrpgPlayer) {
        FileConfiguration classConfig = plugin.getFileConfiguration(classId);

        classConfig.getKeys(true).forEach(x -> Bukkit.getLogger().info(x));

        ConfigurationSection skillSection = classConfig.getConfigurationSection("skills");
        if (skillSection == null) {
            Bukkit.getLogger().info("Could not find Skills section in " + classId);
        } else {
            for (String skillId : skillSection.getKeys(false)) {
                ConfigurationSection skillDataSection = skillSection.getConfigurationSection(skillId);

                if (skillDataSection != null) {
                    try {
                        Class<?> clazz = Class.forName("me.xepos.rpg.skills." + skillDataSection.getName());
                        Constructor<?> constructor = clazz.getConstructor(XRPGPlayer.class, ConfigurationSection.class, XRPG.class);

                        //The instance of the skill automatically assigns itself to the XRPGPlayer
                        constructor.newInstance(xrpgPlayer, skillDataSection, plugin);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.getLogger().info("Something went wrong for " + skillDataSection.getString("name", skillDataSection.getName()));
                    }
                }
            }
        }
        xrpgPlayer.setClassId(classId);
    }

    public HashMap<String, FileConfiguration> initializeClasses() {
        HashMap<String, FileConfiguration> configurationHashMap = new HashMap<>();

        for (File file : classFolder.listFiles()) {
            if (file.getName().contains(".yml")) {

                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
                boolean isDefault = fileConfiguration.getBoolean("default", false);
                boolean isEnabled = fileConfiguration.getBoolean("enabled", true);

                if (!isEnabled) continue; //Skip is class is disabled

                String fileName = file.getName().replace(".yml", "");

                //First enabled class tagged with default is the default class
                if (plugin.getDefaultClassId() == null && isDefault) {
                    plugin.setDefaultClassId(fileName);
                }

                configurationHashMap.put(fileName, fileConfiguration);
            }
            Bukkit.getLogger().info("Loaded " + file.getName());
        }
        return configurationHashMap;
    }

    @SuppressWarnings("ConstantConditions")
    public List<ItemStack> initializeMenu() {
        List<ItemStack> menuItems = new ArrayList<>();

        for (String classId : plugin.getClassData().keySet()) {
            ConfigurationSection displaySettings = plugin.getFileConfiguration(classId);

            if (displaySettings != null) {

                String materialString = displaySettings.getString("icon", "BARRIER");
                //Not sure why it complains about this potentially being null but here we are
                if (materialString == null) {
                    materialString = "BARRIER";
                }

                Material material = Material.getMaterial(materialString);
                List<String> description = displaySettings.getStringList("description");

                ItemStack icon = new ItemStack(material);
                ItemMeta meta = icon.getItemMeta();
                if (meta != null) {
                    meta.setLore(description);
                    meta.setDisplayName(displaySettings.getString("name", "???"));
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "classId"), PersistentDataType.STRING, classId);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    icon.setItemMeta(meta);
                }
                menuItems.add(icon);
            }

        }
        return menuItems;
    }
}
