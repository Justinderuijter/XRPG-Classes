package me.xepos.rpg.classes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.classes.skills.bard.*;
import me.xepos.rpg.configuration.BardConfig;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("unchecked")
public class Bard extends XRPGClass {
    public Bard(XRPG plugin) {
        super(plugin);
    }

    private final BardConfig bardConfig = BardConfig.getInstance();

    private final EnchantedGoldenAppleAoE eGoldenAppleAoE = new EnchantedGoldenAppleAoE(plugin, "Enchanted Golden Apple AoE");
    private final GoldenAppleAoE goldenAppleAoE = new GoldenAppleAoE(plugin, "Golden Apple AoE", eGoldenAppleAoE);
    private final PotionAoE potionAoE = new PotionAoE(plugin, "Potion AoE");
    private final PhoenixBlessing phoenixBlessing = new PhoenixBlessing(plugin, "Phoenix Blessing");
    private final Ballad ballad = new Ballad(plugin, "Ballad");

    private long soundBarrierCooldown = Utils.setSkillCooldown(bardConfig.soundBarrierCooldown);
    private boolean isSoundBarrierActive = false;

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        if (isSoundBarrierActive) {
            e.setCancelled(true);
            return;
        }

        e.setDamage(e.getDamage() * bardConfig.damageMultiplier);
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getEntity();
        if (e.getFinalDamage() >= player.getHealth()) {
            if (!Utils.isSkillReady(soundBarrierCooldown))
                return;
            isSoundBarrierActive = true;
            player.sendMessage(ChatColor.DARK_GREEN + "Sound Barrier prevented your death!");
            e.setDamage(0);
            player.setNoDamageTicks(bardConfig.soundBarrierDuration * 20);
            soundBarrierCooldown = Utils.setSkillCooldown(bardConfig.soundBarrierCooldown);
            new BukkitRunnable() {
                @Override
                public void run() {
                    isSoundBarrierActive = false;
                }
            }.runTaskLater(plugin, bardConfig.soundBarrierDuration * 20L);
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

        switch (e.getItem().getType()) {
            case ENCHANTED_GOLDEN_APPLE:
                eGoldenAppleAoE.activate(e);
                break;
            case GOLDEN_APPLE:
                goldenAppleAoE.activate(e);
                break;
            case POTION:
                potionAoE.activate(e);
                break;
            default:
                break;

        }
    }

    @Override
    public void onUseItem(PlayerInteractEvent e) {
        String heldItemName = e.getPlayer().getInventory().getItemInMainHand().getType().toString().toLowerCase();
        if (heldItemName.contains("axe") || heldItemName.contains("_sword") || heldItemName.contains("_shovel")) {
            //These check for sneaking/not sneaking inside
            phoenixBlessing.activate(e);
            ballad.activate(e);
        }
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

}
