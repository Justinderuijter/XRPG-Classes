package me.xepos.rpg;

import me.xepos.rpg.enums.ModifierType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import java.util.HashMap;

public class AttributeModifierManager {
    private static AttributeModifierManager instance;

    private final HashMap<AttributeModifier, Attribute> positiveModifiers = new HashMap<>();
    private final HashMap<AttributeModifier, Attribute> negativeModifiers = new HashMap<>();

    public HashMap<AttributeModifier, Attribute> getModifiers(ModifierType modifierType) {
        if (modifierType == ModifierType.NEGATIVE) {
            return negativeModifiers;
        } else {
            return positiveModifiers;
        }
    }

    public void put(ModifierType modifierType, AttributeModifier modifier, Attribute attribute) {
        if (modifierType == ModifierType.NEGATIVE) {
            if (!positiveModifiers.containsKey(modifier))
                positiveModifiers.put(modifier, attribute);
        } else {
            if (!negativeModifiers.containsKey(modifier))
                negativeModifiers.put(modifier, attribute);
        }
    }

    public static AttributeModifierManager getInstance() {
        if (instance == null)
            instance = new AttributeModifierManager();

        return instance;
    }


}
