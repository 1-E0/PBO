package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

public class BleedEffect extends StatusEffect {
    private int damagePerTurn;

    public BleedEffect(int duration, int damagePerTurn) {
        super("Bleed", duration);
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public void onTurnEnd(Hero target) {
        System.out.println(target.getName() + " takes " + damagePerTurn + " damage from bleeding!");
        target.takeDamage(damagePerTurn);
    }
}
