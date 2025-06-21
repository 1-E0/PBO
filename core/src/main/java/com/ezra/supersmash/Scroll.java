package com.ezra.supersmash;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Kelas dasar untuk semua scroll
public abstract class Scroll {
    protected String name;
    protected String description;
    protected Texture texture;

    public Scroll(String name, String description, String texturePath) {
        this.name = name;
        this.description = description;
        this.texture = new Texture(texturePath); // Memuat gambar untuk scroll
    }

    // Metode ini akan dipanggil saat scroll digunakan
    // 'user' adalah pemain yang menggunakan scroll, 'screen' adalah instance BattleScreen
    // 'target' adalah hero spesifik yang menjadi sasaran scroll
    public abstract void activate(Player user, Player opponent, Hero target, BattleScreen screen);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }



    public void dispose() {
        texture.dispose();
    }

    public TextureRegion getTexture() {
        return new TextureRegion(texture);
    }
}
