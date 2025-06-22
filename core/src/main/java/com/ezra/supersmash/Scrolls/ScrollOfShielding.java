package com.ezra.supersmash.Scrolls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Effects.DefenseUpEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScrollOfShielding extends Scroll {
    private Sound scrollShield;
    public ScrollOfShielding() {
        // Deskripsi diubah dari "random ally" menjadi "selected ally"
        super("Scroll of Shielding", "Grants a selected ally 50% damage reduction for 1 turn.", "scrolls/shield_scroll.png");
        scrollShield= Gdx.audio.newSound(Gdx.files.internal("sounds/scrollshield.mp3"));
    }

    @Override
    // Metode activate sekarang menerima Hero target
    public void activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        // Logika pemilihan acak dihapus.
        // Pastikan target valid (milik sendiri dan masih hidup).
        if (target != null && target.isAlive() && user.getHeroRoster().contains(target)) {
            scrollShield.play(0.1f);
            target.addStatusEffect(new DefenseUpEffect(2, 0.5f)); // Durasi 2 agar aktif 1 giliran penuh
            screen.log(target.getName() + " is shielded by " + getName() + "!");
            screen.playEffectAnimation(
                target,                     // Hero target
                "effects/shield.png",         // Path ke spritesheet Anda
                2,                         // Jumlah frame per baris (horizontal)
                1,                         // Jumlah total baris animasi di gambar
                0,                          // Indeks baris yang mau dipakai (0-9), 3 untuk baris hijau
                0.12f                       // Durasi per frame (detik)
            );
        } else {
            screen.log(getName() + " failed. No valid target was selected.");
        }
    }
}
