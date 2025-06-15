package com.ezra.supersmash;

// Kelas ini menyimpan hasil dari sebuah reaksi elemental.
public class ReactionResult {
    private float damageMultiplier;
    private String message;
    private StatusEffect newEffect; // Efek status baru yang mungkin dihasilkan (misal: Burn)

    public ReactionResult(float damageMultiplier, String message, StatusEffect newEffect) {
        this.damageMultiplier = damageMultiplier;
        this.message = message;
        this.newEffect = newEffect;
    }

    // Overload constructor untuk reaksi yang tidak menghasilkan efek baru
    public ReactionResult(float damageMultiplier, String message) {
        this(damageMultiplier, message, null);
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public String getMessage() {
        return message;
    }

    public StatusEffect getNewEffect() {
        return newEffect;
    }
}
