package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;
import com.ezra.supersmash.Effects.DefenseUpEffect;

public class ShieldBash implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Shield Bash!");
        target.takeDamage(5);
        self.addStatusEffect(new DefenseUpEffect(1, 0.5f));
    }
}
