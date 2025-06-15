package com.ezra.supersmash.Rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Player;

public class HeroActor extends Actor {
    private Player player;
    private boolean flipX = false;

    public HeroActor(Player player, boolean flipX) {
        this.player = player;
        this.flipX = flipX;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Update the animation state time for the currently active hero
        player.getActiveHero().animationComponent.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Hero hero = player.getActiveHero();
        if (hero == null) return;

        TextureRegion currentFrame = hero.animationComponent.getFrame();

        // Flip the texture region if it's the opponent and it's not already flipped
        if (flipX && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!flipX && currentFrame.isFlipX()) {
            currentFrame.flip(true, false); // Un-flip if necessary
        }

        // Draw the hero's current animation frame
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }
}
