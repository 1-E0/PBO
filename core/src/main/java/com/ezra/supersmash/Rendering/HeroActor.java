package com.ezra.supersmash.Rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ezra.supersmash.Hero;

// HeroActor sekarang kembali menjadi Actor biasa.
public class HeroActor extends Actor {
    private final Hero hero;
    private final boolean flipX;

    public HeroActor(Hero hero, boolean flipX) {
        this.hero = hero;
        this.flipX = flipX;
    }

    public Hero getHero() {
        return hero;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Tetap update animasi untuk hero
        hero.animationComponent.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (hero == null || !hero.isAlive()) return; // Jangan gambar jika hero sudah mati

        TextureRegion currentFrame = hero.animationComponent.getFrame();

        // Flip tekstur jika diperlukan (untuk Player 2)
        if (flipX && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!flipX && currentFrame.isFlipX()) {
            currentFrame.flip(true, false); // Kembalikan jika tidak perlu di-flip
        }

        // Gambar animasi hero
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }
}
