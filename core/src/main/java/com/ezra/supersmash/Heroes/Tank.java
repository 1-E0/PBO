package com.ezra.supersmash.Heroes;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skills.ShieldWall;

public class Tank extends Hero {
    public Tank() {
        super("Tank", 160, new ShieldWall());
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " charges forward!");
        target.takeDamage(10);
    }
}
