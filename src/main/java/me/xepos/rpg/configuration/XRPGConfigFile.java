package me.xepos.rpg.configuration;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class XRPGConfigFile
{
    File file;
    private static FileConfiguration configFile;

    protected XRPGConfigFile(){
        setup();
        setDefaults();
        get().options().copyDefaults(true);
        save();
        loadValues();
    }


    void setup()
    {
        boolean classFileExists = true;
        File rpgFolder = Bukkit.getServer().getPluginManager().getPlugin("RPG").getDataFolder();
        File classFolder = new File(rpgFolder, "Classes");
        file = new File(classFolder,  this.toString() +".yml");

        if (!file.exists())
        {
            classFileExists = false;
            if (!rpgFolder.exists())
            {
                rpgFolder.mkdir();
            }
            if (!classFolder.exists())
            {
                classFolder.mkdir();
            }
            try {
                if(file.createNewFile())
                {
                    System.out.println("Created " + this.toString() + ".yml");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (classFileExists)
            System.out.println("Loaded " + this.toString() + ".yml");

        configFile = YamlConfiguration.loadConfiguration(file);
    }

    void save()
    {
        try
        {
            configFile.save(file);
        }catch(IOException e)
        {
            System.out.println("Could not save " + this.toString() + "!");
        }
    }

    FileConfiguration get()
    {
        return configFile;
    }

    abstract void loadValues();

    abstract void setDefaults();

    @Override
    public String toString(){
        return this.getClass().getSimpleName();
    }
}
