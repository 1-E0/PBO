package com.ezra.supersmash.Scrolls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Effects.AttackUpEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

// Buff Attack, kebalikan dari AttackDownEffec
public class ScrollOfPower extends Scroll {
    private Sound scrollPower;

    public ScrollOfPower() {
        // Deskripsi diubah dari "random ally" menjadi "a selected ally"
        super("Scroll of Power", "Increases a selected ally's attack by 30% for 2 turns.", "scrolls/power_scroll.png");
        scrollPower = Gdx.audio.newSound(Gdx.files.internal("sounds/scrollpower.mp3"));
    }

    @Override
    // Metode activate sekarang menerima Hero target
    public boolean activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        if (target != null && target.isAlive() && user.getHeroRoster().contains(target)) { //
            scrollPower.play(0.1f);
            target.addStatusEffect(new AttackUpEffect(3, 0.3f)); //
            screen.log(target.getName() + "'s attack is boosted by " + getName() + "!"); //
            screen.playEffectAnimation(
                target,
                "effects/power.png", //
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
