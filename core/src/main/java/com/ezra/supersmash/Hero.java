package com.ezra.supersmash;

import com.ezra.supersmash.Effects.*;
import com.ezra.supersmash.Rendering.AnimationComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Hero {
    // --- DATA UNTUK CRITICAL HIT ---
    private static final float CRIT_CHANCE = 0.20f; // Peluang 20%
    private static final float CRIT_MULTIPLIER = 2.0f; // Damage 2x lipat

    // --- ANTRIAN UNTUK FLOATING TEXT ---
    public static class DamageInfo {
        public Hero target;
        public int amount;
        public boolean isCritical;

        public DamageInfo(Hero target, int amount, boolean isCritical) {
            this.target = target;
            this.amount = amount;
            this.isCritical = isCritical;
        }
    }
    public static final List<DamageInfo> damageQueue = new ArrayList<>();


    public String name;
    protected int maxHp;
    protected int currentHp;
    protected Skill skill;
    public AnimationComponent animationComponent;
    protected int energy = 0;
    protected int maxEnergy = 3;
    private List<StatusEffect> activeEffects = new ArrayList<>();

    public Hero(String name, int hp, Skill skill, AnimationComponent animationComponent) {
        this.name = name;
        this.maxHp = hp;
        this.currentHp = hp;
        this.skill = skill;
        this.animationComponent = animationComponent;
    }

    protected int calculateDamage(int baseDamage, boolean isCritical) {
        float finalDamage = baseDamage;
        // Terapkan modifikasi dari status Attack Down
        for (StatusEffect effect : this.activeEffects) {
            if (effect instanceof AttackDownEffect) {
                finalDamage *= (1.0f - ((AttackDownEffect) effect).attackReduction);
            }
        }
        // Terapkan damage critical
        if (isCritical) {
            finalDamage *= CRIT_MULTIPLIER;
        }
        return (int) finalDamage;
    }

    public void dealDamage(Hero target, int baseDamage) {
        // Tentukan apakah serangan ini kritikal atau tidak
        boolean isCritical = Math.random() < CRIT_CHANCE;
        // Hitung final damage berdasarkan status penyerang dan status kritikal
        int finalDamage = calculateDamage(baseDamage, isCritical);
        // Target menerima damage
        target.takeDamage(finalDamage, isCritical);
    }

    public abstract void basicAttack(Hero target);

    public void useSkill(Hero target) {
        skill.activate(this, target);
    }

    public int getEnergy() { return energy; }
    public void gainEnergy(int amount) {
        this.energy = Math.min(this.energy + amount, this.maxEnergy);
    }
    public boolean spendEnergy(int amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }

    // Overload untuk damage biasa (seperti dari Burn/Bleed) yang tidak bisa kritikal
    public void takeDamage(int damage) {
        takeDamage(damage, false);
    }

    public void takeDamage(int damage, boolean isCritical) {
        float finalDamage = damage;

        Iterator<StatusEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            if (effect instanceof DefenseUpEffect) {
                finalDamage *= (1.0f - ((DefenseUpEffect) effect).damageReduction);
            }
            if (effect instanceof VulnerableEffect) {
                finalDamage *= ((VulnerableEffect) effect).damageMultiplier;
                iterator.remove();
            }
        }

        int finalDamageInt = (int) finalDamage;

        if (finalDamageInt > 0) {
            this.animationComponent.setState(AnimationComponent.HeroState.HURT);
            // Tambahkan informasi damage ke antrian untuk ditampilkan oleh BattleScreen
            damageQueue.add(new DamageInfo(this, finalDamageInt, isCritical));
        }

        currentHp -= finalDamageInt;
        if (currentHp <= 0) {
            currentHp = 0;
            this.animationComponent.setState(AnimationComponent.HeroState.DEAD);
        }
    }

    public boolean isStunned() {
        for (StatusEffect effect : activeEffects) {
            if (effect instanceof StunEffect) {
                return true;
            }
        }
        return false;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public void addStatusEffect(StatusEffect effect) {
        effect.onApply(this);
        this.activeEffects.add(effect);
    }

    public void applyAndDecrementEffects() {
        int damageOverTime = 0;
        Iterator<StatusEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();

            if (effect instanceof VulnerableEffect) {
                continue;
            }

            if (effect instanceof BurnEffect) {
                damageOverTime += 5;
            } else if (effect instanceof BleedEffect) {
                damageOverTime += 5;
            } else {
                effect.onTurnEnd(this);
            }

            effect.decrementDuration();
            if (effect.isExpired()) {
                iterator.remove();
            }
        }

        if (damageOverTime > 0) {
            this.takeDamage(damageOverTime);
        }
    }

    public String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append(name).append("\n");
        status.append("HP: ").append(currentHp).append("/").append(maxHp).append("\n");
        status.append("Energy: ").append(energy).append("/").append(maxEnergy);
        return status.toString();
    }

    public String getName() {
        return name;
    }

    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getMaxEnergy() { return maxEnergy; }

    public List<StatusEffect> getActiveEffects() {
        return activeEffects;
    }
}
