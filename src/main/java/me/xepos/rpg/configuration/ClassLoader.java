package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassLoader {
    private final XRPG plugin;
    private final File rpgFolder = Bukkit.getServer().getPluginManager().getPlugin("RPG").getDataFolder();
    private final File classFolder = new File(rpgFolder, "Classes");

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
        File classFile = new File(classFolder, classId.toLowerCase() + ".yml");
        FileConfiguration classConfig = YamlConfiguration.loadConfiguration(classFile);
        ConfigurationSection skillSection = classConfig.getConfigurationSection("Skills");
        if (skillSection == null) {
            Bukkit.getLogger().info("Could not find Skills section in " + classFile.getName());
        } else {
            for (String skill : skillSection.getKeys(false)) {
                ConfigurationSection skillDataSection = classConfig.getConfigurationSection(skill);
                if (skillDataSection != null) {
                    try {
                        Class<?> clazz = Class.forName("me.xepos.rpg.classes.skills.assassin" + skillDataSection.getName());
                        Constructor<?> constructor = clazz.getConstructor(XRPGPlayer.class, String.class, XRPG.class);

                        //The instance of the skill automatically assigns itself to the XRPGPlayer
                        XRPGSkill xrpgSkill = (XRPGSkill) constructor.newInstance(xrpgPlayer, skillDataSection.getString("name", "--"), plugin);

                    } catch (Exception e) {
                        Bukkit.getLogger().info("Something went wrong for " + skillDataSection.getString("name", "some skill"));
                    }
                }

            }
        }


        xrpgPlayer.setClassId(classId);
    }
}
