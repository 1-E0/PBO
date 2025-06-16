package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

public class VulnerableEffect extends StatusEffect {
    public float damageMultiplier;

    public VulnerableEffect(int duration, float damageMultiplier) {
        super("Vulnerable", duration);
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    public void onTurnEnd(Hero target) {
        // Efek ini tidak melakukan apa-apa di akhir giliran, hanya menunggu untuk dipicu
    }
}
