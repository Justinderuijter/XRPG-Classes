package me.xepos.rpg.commands;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class XRPGDebug implements CommandExecutor {

    private final XRPG plugin;
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
                if(strings.length == 1)
                {
                    switch (strings[0])
                    {
                        case "fireballs":
                            player.sendMessage("Fireballs: " + plugin.fireBalls.size());
                            return true;
                        case "damagetaken":
                            for (DamageTakenSource d : Utils.GetRPG(player).dmgTakenMultipliers.keySet()) {
                                player.sendMessage(Utils.GetRPG(player).dmgTakenMultipliers.get(d).toString());
                            }
                            player.sendMessage("dmgTakenMP" + Utils.GetRPG(player).dmgTakenMultipliers.size());
                            return true;
                        case "modifiers":
                            for (String identifier : AttributeModifierManager.getInstance().getModifiers(ModifierType.POSITIVE).keySet()) {
                                player.sendMessage(AttributeModifierManager.getInstance().get(ModifierType.POSITIVE, identifier).getAttributeModifier().toString());
                            }

                            for (String identifier : AttributeModifierManager.getInstance().getModifiers(ModifierType.NEGATIVE).keySet()) {
                                player.sendMessage(AttributeModifierManager.getInstance().get(ModifierType.NEGATIVE, identifier).getAttributeModifier().toString());
                            }
                            return true;
                        case "players":
                            for (UUID id : XRPG.RPGPlayers.keySet()) {
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
