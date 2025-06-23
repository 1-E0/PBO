package com.ezra.supersmash.Rendering;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class VisualEffectActor extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime = 0f;

    public VisualEffectActor(Animation<TextureRegion> animation) {
        this.animation = animation;

        TextureRegion frame = animation.getKeyFrame(0);
        setSize(frame.getRegionWidth(), frame.getRegionHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (animation.isAnimationFinished(stateTime)) {
            this.remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        // Parameter looping (argumen kedua) di set ke false agar animasi tidak berulang
        batch.draw(animation.getKeyFrame(stateTime, false), getX(), getY(), getWidth(), getHeight());
    }

    /**

     * @param texturePath Path ke file gambar (spritesheet).
     * @param frameCols Jumlah kolom (frame horizontal) dalam spritesheet.
     * @param frameRows Jumlah baris (frame vertikal) dalam spritesheet.
     * @param rowIndex Indeks baris yang ingin digunakan (dimulai dari 0).
     * @param frameDuration Durasi untuk setiap frame.
     * @return Objek Animation.
     */
    public static Animation<TextureRegion> createEffectAnimation(String texturePath, int frameCols, int frameRows, int rowIndex, float frameDuration) {
        Texture sheet = new Texture(texturePath);

        // Hitung lebar dan tinggi satu frame
        int frameWidth = sheet.getWidth() / frameCols;
        int frameHeight = sheet.getHeight() / frameRows;

        // Potong-potong spritesheet menjadi grid 2D
        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);

        // Ambil hanya frame dari baris yang diinginkan (rowIndex)
        if (rowIndex >= tmp.length || rowIndex < 0) {
            // Pengaman jika rowIndex di luar batas, gunakan baris pertama
            rowIndex = 0;
        }
        TextureRegion[] frames = new TextureRegion[frameCols];
        System.arraycopy(tmp[rowIndex], 0, frames, 0, frameCols);

        return new Animation<>(frameDuration, frames);
    }
}
