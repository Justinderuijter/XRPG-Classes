package me.xepos.rpg.handlers;

import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;

public class ShootBowEventHandler extends EventHandler {
    private byte currentIndex = 0;

    public void next() {
        if (!getSkills().isEmpty()) {
            currentIndex++;
            if (currentIndex > getSkills().size() - 1) {
                currentIndex = 0;
            }
        }
    }

    public XRPGSkill getCurrentSkill() {
        return getSkills().get(currentIndex);
    }

    @Override
    public void clear() {
        super.clear();
        currentIndex = 0;
    }

    @Override
    public void invoke(Event e) {
        //TODO: always active passive skills e.g. focus
        if (getSkills().size() > 0) {
            getCurrentSkill().activate(e);
        }
    }
}
