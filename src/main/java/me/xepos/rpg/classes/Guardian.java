package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.tasks.RemoveDTModifierTask;
import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.GuardianConfig;
import me.xepos.rpg.tasks.ApplyStunTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class Guardian extends XRPGClass {
    public Guardian(XRPG plugin) {
        super(plugin);
    }

    private final GuardianConfig guardianConfig = GuardianConfig.getInstance();

    private long aegisCooldown = Utils.setSkillCooldown(guardianConfig.aegisCooldown);
    private long shieldBashCooldown = Utils.setSkillCooldown(guardianConfig.shieldBashCooldown);

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getDamager();
        if (player.isSneaking()) {
            doShieldBash(e, player);
        }

        List<Entity> entities = e.getEntity().getNearbyEntities(guardianConfig.mobAggroRangeOnHit, guardianConfig.mobAggroRangeOnHit, guardianConfig.mobAggroRangeOnHit);
        for (Entity entity : entities) {
            if (entity instanceof Monster) {
                player.sendMessage(entity.getName() + "is now targeting you!");
                ((Monster) entity).setTarget(player);
            }
        }
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getEntity();
        double dmg = e.getDamage() * guardianConfig.dmgTakenMultiplier;
        e.setDamage(dmg);

        TextComponent text = new TextComponent("Damage taken reduced by " + String.format(
                Locale.GERMAN, "%,.2f", dmg));
        text.setColor(ChatColor.GREEN.asBungee());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, text);

        if (player.isBlocking() && e.getDamager() instanceof LivingEntity) {
            Utils.decreaseHealth((LivingEntity) e.getDamager(), 1);
        }
    }

    @Override
    public void onJoin(PlayerJoinEvent e) {
        applyEffects(e.getPlayer());
    }

    @Override
    public void onRespawn(PlayerRespawnEvent e) {
        applyEffects(e.getPlayer());
    }

    @Override
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {

    }

    @Override
    public void onUseItem(PlayerInteractEvent e) {
        doAegis(e);
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

    }

    @Override
    public void onInteractWithEntity(PlayerInteractEntityEvent e) {

    }

    @Override
    public void onShootBow(EntityShootBowEvent e) {

    }

    public void applyEffects(Player player) {
        double currentHealth = player.getHealth();
        super.applyEffects(player);
        Utils.addUniqueModifier(player, Attribute.GENERIC_MAX_HEALTH, guardianConfig.healthModifier);
        //This prevents guardian hp to resetting to 20 when it's actually above 20
        player.setHealth(currentHealth);
    }

    @SuppressWarnings("all")
    private void doAegis(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.isSneaking()) {
            if (player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
                if (!Utils.isSkillReady(aegisCooldown)) {
                    player.sendMessage(Utils.getCooldownMessage("Aegis", aegisCooldown));
                    return;
                }
                List<Player> nearbyPlayers = new ArrayList(player.getWorld().getNearbyEntities(player.getLocation(), guardianConfig.aegisRangeHorizontal, guardianConfig.aegisRangeVertical, guardianConfig.aegisRangeHorizontal, p -> p instanceof Player && partyManager.isPlayerAllied(player, (Player) p)));
                for (Player target : nearbyPlayers) {
                    XRPGPlayer xrpgTarget = Utils.GetRPG(target);
                    //Can these be ran async?
                    xrpgTarget.dmgTakenMultipliers.put(DamageTakenSource.AEGIS, guardianConfig.aegisDmgReduction);
                    target.sendMessage(player.getDisplayName() + " Granted you Aegis' Protection!");

                    new RemoveDTModifierTask(target, DamageTakenSource.AEGIS).runTaskLater(plugin, guardianConfig.aegisDuration * 20);

                }
                if (nearbyPlayers.size() > 0) {
                    player.sendMessage(ChatColor.GREEN + "Applied Aegis' Protection to " + nearbyPlayers.size() + " player(s)!");
                    aegisCooldown = Utils.setSkillCooldown(guardianConfig.aegisCooldown);
                }
            }
        }
    }

    private void doShieldBash(EntityDamageByEntityEvent e, Player player) {
        if (e.getEntity() instanceof Player) {
            if (!Utils.isSkillReady(shieldBashCooldown)) {
                player.sendMessage(Utils.getCooldownMessage("Shield Bash", shieldBashCooldown));
                return;
            }

            XRPGPlayer xrpgPlayer = Utils.GetRPG((Player) e.getEntity());
            if (xrpgPlayer.canBeStunned())
                new ApplyStunTask(xrpgPlayer, guardianConfig.stunEffectModifier, guardianConfig.shieldBashDuration * 20, plugin).runTaskLater(plugin, 5);

            shieldBashCooldown = Utils.setSkillCooldown(guardianConfig.shieldBashCooldown);
        }
    }
}
