package com.ezra.supersmash.Heroes;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skills.ShadowStrike;

public class Assassin extends Hero {
    public Assassin() {
        super("Assassin", 70, new ShadowStrike());
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " slashes swiftly!");
        target.takeDamage(25);
    }
}
