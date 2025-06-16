package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

public class AttackDownEffect extends StatusEffect {
    public float attackReduction;

    public AttackDownEffect(int duration, float attackReduction) {
        super("Attack Down", duration);
        this.attackReduction = attackReduction;
    }

    @Override
    public void onTurnEnd(Hero target) {
    }
}
