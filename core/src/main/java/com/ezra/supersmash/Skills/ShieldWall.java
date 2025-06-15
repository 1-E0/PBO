package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class ShieldWall implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " activates Shield Wall! Gains 20 HP.");
        self.takeDamage(-20); // Using negative damage to heal
    }
}
