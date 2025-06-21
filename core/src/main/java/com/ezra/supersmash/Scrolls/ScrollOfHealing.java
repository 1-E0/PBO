package com.ezra.supersmash.Scrolls;

import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.Random;

public class ScrollOfHealing extends Scroll {
    public ScrollOfHealing() {
        super("Scroll of Healing", "Heals a selected ally for 30 HP.", "scrolls/healing_scroll.png");
    }

    @Override
    public void activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        // Gunakan target yang dipilih, pastikan target adalah milik pengguna scroll
        if (target != null && target.isAlive() && user.getHeroRoster().contains(target)) {
            int healAmount = 30;
            target.gainHealth(healAmount);
            screen.log("Used " + getName() + " on " + target.getName() + ", restoring " + healAmount + " HP!");
        } else {
            screen.log(getName() + " fizzles out. No valid target was selected.");
        }
    }
}
