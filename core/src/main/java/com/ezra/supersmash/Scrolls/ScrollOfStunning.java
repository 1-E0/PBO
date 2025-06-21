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
        // Deskripsi diubah dari "random enemy" menjadi "selected enemy"
        super("Scroll of Stunning", "Stuns a selected enemy for 1 turn.", "scrolls/stun_scroll.png");
    }

    @Override
    // Metode activate sekarang menerima Hero target
    public void activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        // Logika pemilihan acak dihapus.
        // Sekarang kita langsung menggunakan target yang dipilih.
        // Pastikan target valid (milik lawan dan masih hidup).
        if (target != null && target.isAlive() && opponent.getHeroRoster().contains(target)) {
            target.addStatusEffect(new StunEffect(2)); // Durasi 2 agar aktif untuk 1 giliran penuh lawan
            screen.log(target.getName() + " is stunned by " + getName() + "!");
        } else {
            screen.log(getName() + " failed. No valid target was selected.");
        }
    }
}
