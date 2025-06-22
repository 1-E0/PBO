package com.ezra.supersmash.Scrolls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

import java.util.Random;

public class ScrollOfHealing extends Scroll {
    private Sound scrollHeal;
    public ScrollOfHealing() {
        super("Scroll of Healing", "Heals a selected ally for 30 HP.", "scrolls/healing_scroll.png");
        scrollHeal = Gdx.audio.newSound(Gdx.files.internal("sounds/scrollheal.mp3"));
    }

    public boolean activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        if (target != null && target.isAlive() && user.getHeroRoster().contains(target)) { //
            scrollHeal.play(0.1f);
            int healAmount = 30;
            target.gainHealth(healAmount); //
            screen.log("Used " + getName() + " on " + target.getName() + ", restoring " + healAmount + " HP!"); //
            screen.playEffectAnimation(
                target,
                "effects/heal.png", //
                5,
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
