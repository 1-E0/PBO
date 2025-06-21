package com.ezra.supersmash.Scrolls;

import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Effects.AttackUpEffect;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;
import com.ezra.supersmash.Scroll;

// Buff Attack, kebalikan dari AttackDownEffec
public class ScrollOfPower extends Scroll {

    public ScrollOfPower() {
        // Deskripsi diubah dari "random ally" menjadi "a selected ally"
        super("Scroll of Power", "Increases a selected ally's attack by 30% for 2 turns.", "scrolls/power_scroll.png");
    }

    @Override
    // Metode activate sekarang menerima Hero target
    public void activate(Player user, Player opponent, Hero target, BattleScreen screen) {
        // Logika pemilihan acak dihapus.
        // Pastikan target valid (milik sendiri dan masih hidup).
        if (target != null && target.isAlive() && user.getHeroRoster().contains(target)) {
            target.addStatusEffect(new AttackUpEffect(3, 0.3f)); // Durasi 3 agar aktif 2 giliran penuh
            screen.log(target.getName() + "'s attack is boosted by " + getName() + "!");
        } else {
            screen.log(getName() + " failed. No valid target was selected.");
        }
    }
}
