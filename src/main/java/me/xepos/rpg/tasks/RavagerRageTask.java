package me.xepos.rpg.tasks;

import me.xepos.rpg.Utils;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.Ravager;
import me.xepos.rpg.classes.XRPGClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

//This task should be ran async considering it doesn't call Bukkit API
public class RavagerRageTask extends BukkitRunnable
{
    private Ravager ravager;
    private Player player;
    private byte count;

    public RavagerRageTask(Player player, byte count)
    {
        XRPGClass playerClass = Utils.GetRPG(player).getPlayerClass();
        if (playerClass instanceof Ravager) {
            this.player = player;
            this.ravager = (Ravager) playerClass;
            this.count = count;
        }
    }

    @Override
    public void run() {
        ravager.decreaseCurrentRage(count);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Current Rage: " + ravager.getCurrentRage() + " (-)", ChatColor.RED));
        if (ravager.getCurrentRage() <= 0)
        {
            this.cancel();
        }
    }
}
