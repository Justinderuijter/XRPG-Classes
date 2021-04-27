package me.xepos.rpg.handlers;

import me.xepos.rpg.skills.XRPGSkill;

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
}
