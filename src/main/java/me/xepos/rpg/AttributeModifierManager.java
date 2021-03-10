package me.xepos.rpg;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import java.util.HashMap;
import java.util.UUID;

public class AttributeModifierManager {
    private static final HashMap<AttributeModifier, Attribute> modifiers = new HashMap<>();

    public static HashMap<AttributeModifier, Attribute> getModifiers() {
        return modifiers;
    }

    public static void put(AttributeModifier modifier, Attribute attribute)
    {
        if(!modifiers.containsKey(modifier))
            modifiers.put(modifier, attribute);
    }
}
