package com.ezra.supersmash.Heroes;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skills.Fireball;

public class Mage extends Hero {
    public Mage() {
        super("Mage", 80, new Fireball());
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " attacks with magic missile!");
        target.takeDamage(15);
    }
}
