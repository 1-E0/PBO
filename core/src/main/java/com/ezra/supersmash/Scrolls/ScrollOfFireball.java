package com.ezra.supersmash.Scrolls;

import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScrollOfFireball extends Scroll {
    public ScrollOfFireball() {
        super("Scroll of Fireball", "Deals 40 damage to a selected enemy.", "scrolls/fireball_scroll.png");
    }

    @Override
    public void activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        // Logika acak tidak lagi diperlukan
        if (target != null && target.isAlive() && opponent.getHeroRoster().contains(target)) {
            int damage = 40;
            target.takeDamage(damage); // Langsung memberikan damage tanpa critical
            screen.log(getName() + " hits " + target.getName() + " for " + damage + " damage!");
        } else {
            screen.log(getName() + " fizzles out. No valid target was selected.");
        }
    }
}
