package me.xepos.rpg.classes;

import me.xepos.rpg.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.BrawlerConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@SuppressWarnings("unused")
public class Brawler extends XRPGClass {

    private final BrawlerConfig brawlerConfig = BrawlerConfig.getInstance();

    private byte hitCount = 0;
    private final List<PotionEffect> defEffects = new ArrayList<>();
    private final List<PotionEffect> dmgEffects = new ArrayList<>();


    public Brawler(XRPG plugin) {
        super(plugin);
        //Offense
        dmgEffects.add(new PotionEffect(PotionEffectType.SATURATION, brawlerConfig.effectDuration * 20, 0, false, false, true));
        dmgEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, brawlerConfig.effectDuration * 20, 0, false, false, true));
        dmgEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, brawlerConfig.effectDuration * 20, 1, false, false, true));
        dmgEffects.add(new PotionEffect(PotionEffectType.SPEED, brawlerConfig.effectDuration * 20, 0, false, false, true));
        //Defense
        defEffects.add(new PotionEffect(PotionEffectType.REGENERATION, brawlerConfig.effectDuration * 20, 1, false, false, true));
        defEffects.add(new PotionEffect(PotionEffectType.SLOW_FALLING, brawlerConfig.effectDuration * 20, 0, false, false, true));
        defEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, brawlerConfig.effectDuration * 20, 0, false, false, true));
        defEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, brawlerConfig.effectDuration * 20, 0, false, false, true));

    }

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getDamager();
        double armor = player.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
        HashMap<Enchantment, Integer> enchantValues = getProtection(player);
        int protectionLevel = enchantValues.get(Enchantment.PROTECTION_ENVIRONMENTAL);
        int fireProtectionLevel = enchantValues.get(Enchantment.PROTECTION_FIRE);
        int explosionProtectionLevel = enchantValues.get(Enchantment.PROTECTION_EXPLOSIONS);
        int projectileProtectionLevel = enchantValues.get(Enchantment.PROTECTION_EXPLOSIONS);

        double fistDamage = brawlerConfig.fistBaseDamage + (armor * brawlerConfig.armorToDamageRatio) + (toughness * brawlerConfig.toughnessToDamageRatio) + (protectionLevel * brawlerConfig.protectionToDamageRatio);

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            e.setDamage(fistDamage);
            if (isCrit(explosionProtectionLevel))
            {
                player.sendMessage(ChatColor.GREEN + "You hit a weak spot and dealt bonus damage!");
                e.setDamage(fistDamage * brawlerConfig.vitalModifier);
            }
            doLotusHaste(projectileProtectionLevel);
            setFire(e, fireProtectionLevel);
            if (canUseLotus(player))
            {
                //Lotus Strike logic
                double originalDamage = e.getDamage();
                double lotusDamage = originalDamage * brawlerConfig.lotusModifier;
                e.setDamage(lotusDamage);
                player.sendMessage(ChatColor.GREEN + "Lotus strike dealt " + String.format("%,.2f",lotusDamage - originalDamage) + " bonus damage!");
                player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1F, 0.5F);
                applyTriggerEffect(player, true);
            } else {
                //If !canUseLotus
                incrementHitCount();
            }
        } else {
            e.setDamage(brawlerConfig.nonFistDamage); //item isn't hand
        }
    }

    @Override
    public void onHurt(EntityDamageByEntityEvent e) {

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
        Player player = e.getPlayer();
        //Air doesn't work with empty hand so not point in checking.
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {

                    if (canUseLotus(player)) {
                        useInnerStrength(player);
                    }
            }
        }
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

    }

    @Override
    public void onInteractWithEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof LivingEntity && !(e.getRightClicked() instanceof Villager)) {
            if (canUseLotus(e.getPlayer())) {
                useInnerStrength(e.getPlayer());
            }
        }
    }

    @Override
    public void onShootBow(EntityShootBowEvent e) {

    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    private boolean canUseLotus(Player player) {
        if (hitCount == brawlerConfig.triggerAmount - 1) {
            player.sendMessage(ChatColor.DARK_GREEN + "Lotus techniques are ready to be used!");
            return false;
        }
        return hitCount >= brawlerConfig.triggerAmount;
    }

    private void incrementHitCount() {
        if (hitCount < brawlerConfig.triggerAmount) {
            hitCount++;
        }
    }

    public void useInnerStrength(Player player) {
        applyTriggerEffect(player, false);
        healPlayer(player);//Prevents error if health > MaxHealth
        player.sendMessage("Inner Strength healed you for " + brawlerConfig.innerStrengthHealAmount);
    }

    private void applyTriggerEffect(Player player, boolean isDmgEffect) {
        Random rand = new Random();
        if (isDmgEffect) {
            player.addPotionEffect(dmgEffects.get(rand.nextInt(dmgEffects.size())));
        } else {
            player.addPotionEffect(defEffects.get(rand.nextInt(defEffects.size())));
        }
        hitCount = 0;
    }

    private void healPlayer(Player player) {
        Utils.healLivingEntity(player, brawlerConfig.innerStrengthHealAmount);
    }

    private HashMap<Enchantment, Integer> getProtection(Player player) {
        ArrayList<ItemStack> armor = new ArrayList<ItemStack>() {{
            add(player.getEquipment().getHelmet());
            add(player.getEquipment().getChestplate());
            add(player.getEquipment().getLeggings());
            add(player.getEquipment().getBoots());
        }};
        HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>() {{
            put(Enchantment.PROTECTION_ENVIRONMENTAL, 0);
            put(Enchantment.PROTECTION_EXPLOSIONS, 0);
            put(Enchantment.PROTECTION_FIRE, 0);
            put(Enchantment.PROTECTION_PROJECTILE, 0);
        }};
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            for (ItemStack armorPiece : armor) {
                if (armorPiece != null && armorPiece.containsEnchantment(enchantment)) {
                    int enchantLevel = armorPiece.getEnchantmentLevel(enchantment);
                    enchantments.replace(enchantment, enchantments.get(enchantment) + enchantLevel);
                }
            }
        }
        return enchantments;
    }

    @SuppressWarnings("all")
    private boolean isCrit(int enchantLevel) {
        Random rand = new Random();
        if (rand.nextInt(100) + 1 <= enchantLevel * brawlerConfig.enchantToCritRatio) //0-100
        {
            return true;
        }
        return false;
    }

    private void setFire(EntityDamageByEntityEvent e, int enchantLevel) {
        if (enchantLevel > 0 && e.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) e.getEntity();
            if (entity.getFireTicks() <= -1) {
                entity.setFireTicks(enchantLevel * brawlerConfig.fireTicksPerEnchantLevel);
            }
        }
    }

    private void doLotusHaste(int enchantLevel)
    {
        Random rand = new Random();
        if (rand.nextInt(100) + 1 <= enchantLevel * brawlerConfig.enchantToLotusHasteRatio)
        {
            incrementHitCount();
        }
    }
}

