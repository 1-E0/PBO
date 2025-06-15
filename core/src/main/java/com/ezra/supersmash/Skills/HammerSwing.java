package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Effects.StunEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class HammerSwing implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Hammer Swing!");
        target.takeDamage(20);
        if (Math.random() < 0.5) {
            target.addStatusEffect(new StunEffect(1));
        }
    }
}
