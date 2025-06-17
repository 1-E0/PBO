package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

public class VulnerableEffect extends StatusEffect {
    public float damageMultiplier;

    public VulnerableEffect(float damageMultiplier) {
        // Durasi di-set ke angka yang sangat tinggi (seperti tak terbatas) karena tidak akan pernah berkurang.
        super("Vulnerable", 999);
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    public void onTurnEnd(Hero target) {
        // Efek ini tidak melakukan apa-apa di akhir giliran, hanya menunggu untuk dipicu oleh serangan.
    }
}
