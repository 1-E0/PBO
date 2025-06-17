package com.ezra.supersmash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FloatingText extends Label {

    public FloatingText(String text, Skin skin, Color color) {
        super(text, skin);
        this.setFontScale(1.2f); // Buat teks sedikit lebih besar
        this.setColor(color);
    }

    /**
     * Memulai animasi untuk teks ini.
     * Teks akan bergerak ke atas dan memudar secara bersamaan, lalu hilang.
     */
    public void animate() {
        // Gabungkan dua aksi untuk berjalan secara paralel: bergerak dan memudar
        addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0, 70, 1.5f), // Bergerak ke atas sejauh 70 piksel selama 1.5 detik
                    Actions.fadeOut(1.5f)           // Memudar selama 1.5 detik
                ),
                Actions.removeActor() // Hapus aktor ini dari stage setelah selesai
            )
        );
    }
}
