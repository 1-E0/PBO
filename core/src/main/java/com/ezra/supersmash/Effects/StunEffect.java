package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

public class StunEffect extends StatusEffect {

    public StunEffect(int duration) {
        super("Stunned", duration);
    }

    @Override
    public void onTurnEnd(Hero target) {
    }
}
