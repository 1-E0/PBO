package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

public class DefenseUpEffect extends StatusEffect {
    public float damageReduction;

    public DefenseUpEffect(int duration, float damageReduction) {
        super("Defense Up", duration);
        this.damageReduction = damageReduction;
    }

    @Override
    public void onTurnEnd(Hero target) {
    }
}
