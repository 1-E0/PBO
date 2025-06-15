package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class MultiShot implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Multi Shot!");
        target.takeDamage(20);
    }
}
