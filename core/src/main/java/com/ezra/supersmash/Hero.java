package com.ezra.supersmash;

public abstract class Hero {
    public String name;
    protected int maxHp;
    protected int currentHp;
    protected Skill skill; // Composition: skill dimiliki hero

    public Hero(String name, int hp, Skill skill) {
        this.name = name;
        this.maxHp = hp;
        this.currentHp = hp;
        this.skill = skill;
    }

    public abstract void basicAttack(Hero target);

    public void useSkill(Hero target) {
        skill.activate(this, target);
    }

    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp < 0) currentHp = 0;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public String getStatus() {
        return name + " HP: " + currentHp + "/" + maxHp;
    }

    public String getName() {
        return name;
    }
}
