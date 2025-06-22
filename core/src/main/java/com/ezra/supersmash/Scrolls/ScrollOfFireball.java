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
    public boolean activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        if (target != null && target.isAlive() && opponent.getHeroRoster().contains(target)) { //
            scrollFire.play(0.1f);
            int damage = 40;
            target.takeDamage(damage); //
            screen.log(getName() + " hits " + target.getName() + " for " + damage + " damage!"); //
            screen.playEffectAnimation(
                target,
                "effects/fireball.png", //
                4,
                1,
                0,
                0.08f
            );
            return true; // UBAH: Kembalikan true jika berhasil
        } else {
            screen.log(getName() + " fizzles out. No valid target was selected."); //
            return false; // UBAH: Kembalikan false jika gagal
        }
    }
}
