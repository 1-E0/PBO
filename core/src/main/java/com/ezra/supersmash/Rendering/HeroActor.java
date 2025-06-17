package com.ezra.supersmash.Rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ezra.supersmash.Hero;

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
        hero.animationComponent.update(delta);
    }

    /**
     * Ganti seluruh metode draw() lama dengan yang baru ini.
     * Perbaikan utama ada pada baris: batch.getColor().cpy()
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (hero == null) return;

        TextureRegion currentFrame = hero.animationComponent.getFrame();

        if (flipX && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!flipX && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        // Simpan SALINAN warna asli batch
        Color originalColor = batch.getColor().cpy(); // <-- PERBAIKAN KRUSIAL ADA DI SINI

        if (hero.isStunned()) {
            // Jika hero stun, gambar dengan warna keabu-abuan
            batch.setColor(Color.GRAY);
        }

        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());

        // Kembalikan warna batch ke warna asli agar tidak mempengaruhi gambar lain
        batch.setColor(originalColor);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;

        float hitboxHeight = getHeight() * 0.7f;

        if (x >= 0 && x < getWidth() && y >= 0 && y < hitboxHeight) {
            return this;
        }

        return null;
    }
}
