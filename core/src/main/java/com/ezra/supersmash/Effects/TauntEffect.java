package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

/**
 * Efek ini memaksa lawan untuk menyerang hero yang memiliki efek ini.
 * Logika penegakannya ada di BattleScreen.
 */
public class TauntEffect extends StatusEffect {

    public TauntEffect(int duration) {
        super("Taunt", duration);
    }

    @Override
    public void onTurnEnd(Hero target) {
        // Tidak ada aksi yang terjadi pada akhir giliran untuk Taunt.
    }
}
