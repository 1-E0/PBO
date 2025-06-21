package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;

public class AttackUpEffect extends com.ezra.supersmash.StatusEffect {
    public float attackBonus;

    public AttackUpEffect(int duration, float attackBonus) {
        super("Attack Up", duration);
        this.attackBonus = attackBonus;
    }

    // Logika AttackUpEffect harus diimplementasikan di dalam Hero.calculateDamage
    @Override
    public void onTurnEnd(Hero target) {}
}
