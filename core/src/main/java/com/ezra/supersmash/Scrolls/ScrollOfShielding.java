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
    // UBAH: Mengembalikan boolean
    public boolean activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        if (target != null && target.isAlive() && user.getHeroRoster().contains(target)) { //
            scrollShield.play(0.1f);
            target.addStatusEffect(new DefenseUpEffect(2, 0.5f)); //
            screen.log(target.getName() + " is shielded by " + getName() + "!"); //
            screen.playEffectAnimation(
                target,
                "effects/shield.png", //
                2,
                1,
                0,
                0.12f
            );
            return true; // UBAH: Kembalikan true jika berhasil
        } else {
            screen.log(getName() + " failed. No valid target was selected."); //
            return false; // UBAH: Kembalikan false jika gagal
        }
    }
}
