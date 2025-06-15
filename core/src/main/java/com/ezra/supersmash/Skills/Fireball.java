package com.ezra.supersmash.Skills;

import com.ezra.supersmash.Element;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.ReactionManager;
import com.ezra.supersmash.ReactionResult;
import com.ezra.supersmash.Skill;

public class Fireball implements Skill {
    @Override
    public void activate(Hero self, Hero target) {
        float damage = 15;
        Element triggerElement = self.getElement(); // Elemen skill ini adalah FIRE

        // 1. Proses reaksi elemental
        ReactionResult reaction = ReactionManager.processReaction(target.getAppliedElement(), triggerElement);

        // 2. Terapkan pengganda damage dari hasil reaksi
        damage *= reaction.getDamageMultiplier();

        // Tampilkan pesan di log
        System.out.println(self.getName() + " uses Fireball! " + reaction.getMessage());

        // 3. Jika ada reaksi, elemen dasar pada target akan hilang (dikonsumsi)
        if (reaction.getDamageMultiplier() != 1.0f || reaction.getNewEffect() != null) {
            target.setAppliedElement(Element.NEUTRAL);
        }

        // 4. Terapkan efek status baru jika reaksi menghasilkannya
        if (reaction.getNewEffect() != null) {
            target.addStatusEffect(reaction.getNewEffect());
        }

        // 5. Jika tidak ada reaksi sama sekali, terapkan elemen dari skill ini ke target
        if (target.getAppliedElement() == Element.NEUTRAL && reaction.getDamageMultiplier() == 1.0f) {
            target.setAppliedElement(triggerElement);
        }

        // Terakhir, berikan damage ke target
        target.takeDamage((int) damage);
    }
}
