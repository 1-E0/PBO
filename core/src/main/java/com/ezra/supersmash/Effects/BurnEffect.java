package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

public class BurnEffect extends StatusEffect {
    private int damagePerTurn;

    public BurnEffect(int duration, int damagePerTurn) {
        super("Burn", duration);
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public void onTurnEnd(Hero target) {
        System.out.println(target.getName() + " takes " + damagePerTurn + " damage from being on fire!");
        target.takeDamage(damagePerTurn);
    }
}
