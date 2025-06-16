package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Effects.BurnEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class Fireball implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Fireball!");

        // Berikan damage dasar
        target.takeDamage(15);

        // Terapkan efek Burn (misalnya, 5 damage selama 2 turn)
        target.addStatusEffect(new BurnEffect(2, 5));
    }
}
