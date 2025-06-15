package com.ezra.supersmash.Heroes;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skills.PowerStrike;

public class Warrior extends Hero {
    public Warrior() {
        super("Warrior", 120, new PowerStrike());
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " attacks with sword!");
        target.takeDamage(20);
    }
}
