package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Effects.BleedEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class MultiShot implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Multi Shot!");
        self.dealDamage(target, 20); // Panggil metode publik baru
        target.addStatusEffect(new BleedEffect(2, 5));
    }
}
