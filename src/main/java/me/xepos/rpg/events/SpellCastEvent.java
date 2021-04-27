package me.xepos.rpg.events;

import me.xepos.rpg.skills.XRPGSkill;

public interface SpellCastEvent {
    void onSpellCast(XRPGSkill skill);
}
