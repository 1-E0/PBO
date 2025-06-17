package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Effects.AttackDownEffect;
import com.ezra.supersmash.Effects.DefenseUpEffect;
import com.ezra.supersmash.Effects.TauntEffect; // <-- PERUBAHAN
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Skill;

public class ShieldBash implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        System.out.println(self.getName() + " uses Shield Bash, taunting enemies!"); // <-- PERUBAHAN (Opsional)
        self.dealDamage(target, 5);
        // UBAH DURASI MENJADI 2 AGAR EFEK BERTAHAN SATU GILIRAN LAWAN
        self.addStatusEffect(new DefenseUpEffect(2, 0.5f));
        self.addStatusEffect(new TauntEffect(2)); // <-- PERUBAHAN
        target.addStatusEffect(new AttackDownEffect(1, 0.3f));
    }
}
