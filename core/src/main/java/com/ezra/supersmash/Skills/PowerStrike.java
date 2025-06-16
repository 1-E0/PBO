package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class PowerStrike implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Power Strike!");
        self.dealDamage(target, 35); // Panggil metode publik baru
    }
}
