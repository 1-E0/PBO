package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Effects.BurnEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class Fireball implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Fireball!");
        target.takeDamage(15);
        target.addStatusEffect(new BurnEffect(2, 10));
    }
}
