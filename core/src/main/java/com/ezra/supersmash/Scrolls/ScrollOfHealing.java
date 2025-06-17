package com.ezra.supersmash.Scrolls;

import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.Random;

public class ScrollOfHealing extends Scroll {
    public ScrollOfHealing() {
        // Ganti "path/to/healing_scroll.png" dengan path gambar Anda nanti
        super("Scroll of Healing", "Heals a random ally for 30 HP.", "scrolls/healing_scroll.png");
    }

    @Override
    public void activate(Player user, Player opponent, BattleScreen screen) {
        Hero target = null;
        // Cari hero yang masih hidup dan tidak memiliki HP penuh
        for (Hero hero : user.getHeroRoster()) {
            if (hero.isAlive() && hero.getCurrentHp() < hero.getMaxHp()) {
                target = hero;
                break;
            }
        }

        // Jika tidak ada yang terluka, pilih saja hero acak yang hidup
        if (target == null) {
            for (Hero hero : user.getHeroRoster()) {
                if (hero.isAlive()) {
                    target = hero;
                    break;
                }
            }
        }

        if (target != null) {
            int healAmount = 30;
            target.gainHealth(healAmount); // Anda perlu menambahkan metode gainHealth di Hero.java
            screen.log("Used " + getName() + " on " + target.getName() + ", restoring " + healAmount + " HP!");
        }
    }
}
