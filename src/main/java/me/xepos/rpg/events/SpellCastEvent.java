package me.xepos.rpg.events;

import me.xepos.rpg.classes.skills.XRPGSkill;

public interface SpellCastEvent {
    void onSpellCast(XRPGSkill skill);
}
