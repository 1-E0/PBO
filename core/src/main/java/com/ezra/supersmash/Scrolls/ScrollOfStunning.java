package com.ezra.supersmash.Scrolls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Effects.StunEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScrollOfStunning extends Scroll {
    private Sound scrollStun;
    public ScrollOfStunning() {
        // Deskripsi diubah dari "random enemy" menjadi "selected enemy"
        super("Scroll of Stunning", "Stuns a selected enemy for 1 turn.", "scrolls/stun_scroll.png");
        scrollStun = Gdx.audio.newSound(Gdx.files.internal("sounds/scrollstun.mp3"));
    }

    @Override
    // Metode activate sekarang menerima Hero target
    public boolean activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        // Logika pemilihan acak dihapus.
        // Sekarang kita langsung menggunakan target yang dipilih.
        // Pastikan target valid (milik lawan dan masih hidup).
        if (target != null && target.isAlive() && opponent.getHeroRoster().contains(target)) {
            scrollStun.play(0.1f);
            target.addStatusEffect(new StunEffect(2)); // Durasi 2 agar aktif untuk 1 giliran penuh lawan
            screen.log(target.getName() + " is stunned by " + getName() + "!");
            screen.playEffectAnimation(
                target,                     // Hero target
                "effects/stun.png",         // Path ke spritesheet Anda
                2,                         // Jumlah frame per baris (horizontal)
                1,                         // Jumlah total baris animasi di gambar
                0,                          // Indeks baris yang mau dipakai (0-9), 3 untuk baris hijau
                0.12f                       // Durasi per frame (detik)
            ); return true; // UBAH: Kembalikan true jika berhasil
        } else {
            screen.log(getName() + " fizzles out. No valid target was selected."); //
            return false; // UBAH: Kembalikan false jika gagal
        }
    }
}
