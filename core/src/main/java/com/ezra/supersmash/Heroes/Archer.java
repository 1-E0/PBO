package com.ezra.supersmash.Heroes;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skills.MultiShot;

public class Archer extends Hero {
    public Archer() {
        super("Archer", 90, new MultiShot());
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " shoots an arrow!");
        target.takeDamage(18);
    }
}
