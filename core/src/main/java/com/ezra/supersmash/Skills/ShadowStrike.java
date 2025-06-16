package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Effects.VulnerableEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class ShadowStrike implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Shadow Strike! Ignores defense.");
        self.dealDamage(target, 40); // Panggil metode publik baru
        target.addStatusEffect(new VulnerableEffect(1, 1.5f));
    }
}
