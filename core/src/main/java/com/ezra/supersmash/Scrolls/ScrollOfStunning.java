package com.ezra.supersmash.Scrolls;

import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Effects.StunEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScrollOfStunning extends Scroll {
    public ScrollOfStunning() {
        // Ganti "path/to/stun_scroll.png" dengan path gambar Anda
        super("Scroll of Stunning", "Stuns a random enemy for 1 turn.", "scrolls/stun_scroll.png");
    }

    @Override
    public void activate(Player user, Player opponent, BattleScreen screen) {
        List<Hero> validTargets = new ArrayList<>();
        for (Hero hero : opponent.getHeroRoster()) {
            if (hero.isAlive()) {
                validTargets.add(hero);
            }
        }

        if (!validTargets.isEmpty()) {
            Hero target = validTargets.get(new Random().nextInt(validTargets.size()));
            target.addStatusEffect(new StunEffect(2)); // Durasi 2 agar aktif untuk 1 giliran penuh lawan
            screen.log(target.getName() + " is stunned by " + getName() + "!");
        }
    }
}
