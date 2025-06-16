package com.ezra.supersmash;

import com.ezra.supersmash.Effects.AttackDownEffect;
import com.ezra.supersmash.Effects.DefenseUpEffect;
import com.ezra.supersmash.Effects.StunEffect;
import com.ezra.supersmash.Effects.VulnerableEffect;
import com.ezra.supersmash.Rendering.AnimationComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Hero {
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

    /**
     * Metode internal untuk menghitung damage output akhir setelah memperhitungkan
     * status effect milik penyerang (seperti AttackDownEffect).
     * Tetap protected karena ini adalah kalkulasi internal.
     */
    protected int calculateDamage(int baseDamage) {
        float finalDamage = baseDamage;
        for (StatusEffect effect : this.activeEffects) {
            if (effect instanceof AttackDownEffect) {
                finalDamage *= (1.0f - ((AttackDownEffect) effect).attackReduction);
            }
        }
        return (int) finalDamage;
    }

    /**
     * Metode publik baru untuk menangani proses penyerangan.
     * Metode ini akan memanggil kalkulasi internal dan kemudian memberikan damage ke target.
     * Ini adalah metode yang harus dipanggil oleh Skill dan basicAttack.
     */
    public void dealDamage(Hero target, int baseDamage) {
        int finalDamage = calculateDamage(baseDamage);
        target.takeDamage(finalDamage);
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

    public void takeDamage(int damage) {
        float finalDamage = damage;
        for (StatusEffect effect : activeEffects) {
            if (effect instanceof DefenseUpEffect) {
                finalDamage *= (1.0f - ((DefenseUpEffect) effect).damageReduction);
            }
            if (effect instanceof VulnerableEffect) {
                finalDamage *= ((VulnerableEffect) effect).damageMultiplier;
            }
        }

        if (damage > 0) {
            this.animationComponent.setState(AnimationComponent.HeroState.HURT);
        }

        currentHp -= (int)finalDamage;
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
        Iterator<StatusEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            effect.onTurnEnd(this);
            effect.decrementDuration();
            if (effect.isExpired()) {
                iterator.remove();
            }
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
}
