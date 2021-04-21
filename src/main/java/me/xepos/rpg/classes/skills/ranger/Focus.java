package me.xepos.rpg.classes.skills.ranger;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.classes.skills.XRPGSkill;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Focus extends XRPGSkill {
    private int currentSkill = 0;
    private final List<XRPGBowSkill> bowSkills = new ArrayList<>();

    public Focus(XRPGPlayer xrpgPlayer, String skillName, XRPG plugin, @Nullable HashSet<XRPGBowSkill> bowSkills) {
        super(xrpgPlayer, skillName, plugin);

        if (bowSkills != null && !bowSkills.isEmpty()) {
            for (XRPGBowSkill bowSkill : bowSkills) {
                if (!this.bowSkills.contains(bowSkill))
                    bowSkill.setBelongsToCollection(true);
                this.bowSkills.add(bowSkill);
            }
            //No point in adding this if there aren't any skills
            xrpgPlayer.getLeftClickEventHandler().addSkill(this);
        }

        xrpgPlayer.getShootBowEventHandler().addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof EntityShootBowEvent) {
            EntityShootBowEvent e = (EntityShootBowEvent) event;

            if (e.getProjectile() instanceof Arrow) {
                ((Arrow) e.getProjectile()).setCritical(true);

                if (!bowSkills.isEmpty()) {
                    bowSkills.get(currentSkill).activate(e);
                }
            }
        } else if (event instanceof EntityInteractEvent) {
            increaseCounter();
        } else {
            getXRPGPlayer().getPlayer().sendMessage(ChatColor.DARK_RED + "Something went wrong: Event mismatch!");
            getXRPGPlayer().getPlayer().sendMessage(ChatColor.DARK_RED + "Please report this to a server admin.");
        }
    }

    @Override
    public void initialize() {

    }

    public List<XRPGBowSkill> getBowSkills() {
        return bowSkills;
    }

    public void setBowSkills(HashSet<XRPGBowSkill> bowSkills) {
        this.bowSkills.clear();
        bowSkills.forEach(x -> {
            x.setBelongsToCollection(true);
            this.bowSkills.add(x);
        });
    }

    public void addBowSkill(XRPGBowSkill bowSkill) {
        if (!bowSkills.contains(bowSkill)) {
            bowSkill.setBelongsToCollection(true);
            bowSkills.add(bowSkill);
        }
    }

    private void increaseCounter() {
        currentSkill++;
        if (currentSkill > bowSkills.size() - 1)
            currentSkill = 0;
    }
}
