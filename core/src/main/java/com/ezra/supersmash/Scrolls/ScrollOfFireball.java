package com.ezra.supersmash.Scrolls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScrollOfFireball extends Scroll {
    private Sound scrollFire;
    public ScrollOfFireball() {
        super("Scroll of Fireball", "Deals 40 damage to a selected enemy.", "scrolls/fireball_scroll.png");
        scrollFire = Gdx.audio.newSound(Gdx.files.internal("sounds/scrollfireball.mp3"));
    }

    @Override
    public void activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        // Logika acak tidak lagi diperlukan
        if (target != null && target.isAlive() && opponent.getHeroRoster().contains(target)) {
            scrollFire.play(0.1f);
            int damage = 40;
            target.takeDamage(damage); // Langsung memberikan damage tanpa critical
            screen.log(getName() + " hits " + target.getName() + " for " + damage + " damage!");
            screen.playEffectAnimation(
                target,                     // Hero target
                "effects/fireball.png",         // Path ke spritesheet Anda
                4,                         // Jumlah frame per baris (horizontal)
                1,                         // Jumlah total baris animasi di gambar
                0,                          // Indeks baris yang mau dipakai (0-9), 3 untuk baris hijau
                0.08f                       // Durasi per frame (detik)
            );
        } else {
            screen.log(getName() + " fizzles out. No valid target was selected.");
        }
    }
}
