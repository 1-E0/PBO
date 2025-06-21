package com.ezra.supersmash.Scrolls;

import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Effects.DefenseUpEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScrollOfShielding extends Scroll {
    public ScrollOfShielding() {
        // Deskripsi diubah dari "random ally" menjadi "selected ally"
        super("Scroll of Shielding", "Grants a selected ally 50% damage reduction for 1 turn.", "scrolls/shield_scroll.png");
    }

    @Override
    // Metode activate sekarang menerima Hero target
    public void activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        // Logika pemilihan acak dihapus.
        // Pastikan target valid (milik sendiri dan masih hidup).
        if (target != null && target.isAlive() && user.getHeroRoster().contains(target)) {
            target.addStatusEffect(new DefenseUpEffect(2, 0.5f)); // Durasi 2 agar aktif 1 giliran penuh
            screen.log(target.getName() + " is shielded by " + getName() + "!");
        } else {
            screen.log(getName() + " failed. No valid target was selected.");
        }
    }
}
