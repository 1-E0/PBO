package com.ezra.supersmash.Effects;

import com.ezra.supersmash.Hero;
import com.ezra.supersmash.StatusEffect;

/**
 * Efek ini memaksa lawan untuk menyerang hero yang memiliki efek ini.
 * Logika penegakannya ada di BattleScreen.
 */
public class MockEffect extends StatusEffect {

    public MockEffect(int duration) {
        super("Mock ", duration);
    }

    @Override
    public void onTurnEnd(Hero target) {
        // Tidak ada aksi yang terjadi pada akhir giliran untuk Taunt.
    }
}
