package me.xepos.rpg.commands;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.Necromancer;
import me.xepos.rpg.classes.XRPGClass;
import me.xepos.rpg.enums.DamageTakenSource;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class XRPGDebug implements CommandExecutor {

    private XRPG plugin;
    public XRPGDebug(XRPG plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (command.getName().equals("xrpgdebug")) {
            if(commandSender instanceof Player)
            {
                Player player = (Player) commandSender;
                XRPGClass clazz = Utils.GetRPG(player).getPlayerClass();
                if(strings.length == 1)
                {
                    switch (strings[0])
                    {
                        case "fireballs":
                            player.sendMessage("Fireballs: " + plugin.fireBalls.size());
                            return true;
                        case "followers":

                            if (clazz instanceof Necromancer)
                                player.sendMessage("Followers: " + ((Necromancer)clazz).followers);
                            else
                                player.sendMessage("Not a Necromancer");

                            return true;
                        case "damagetaken":
                            for (DamageTakenSource d:Utils.GetRPG(player).dmgTakenMultipliers.keySet()) {
                                player.sendMessage(Utils.GetRPG(player).dmgTakenMultipliers.get(d).toString());
                            }
                            player.sendMessage("dmgTakenMP" + Utils.GetRPG(player).dmgTakenMultipliers.size());
                            return true;
                        case "modifiers":
                            for (AttributeModifier modifier: AttributeModifierManager.getModifiers().keySet()) {
                                player.sendMessage(modifier.toString());
                            }
                            return true;
                        case "players":
                            for (UUID id:XRPG.RPGPlayers.keySet()) {
                                XRPGPlayer xrpgPlayer = XRPG.RPGPlayers.get(id);
                                player.sendMessage(xrpgPlayer.getPlayer().getName() + ": " + xrpgPlayer.getClassId());
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            }

        }
        return false;
    }
}
