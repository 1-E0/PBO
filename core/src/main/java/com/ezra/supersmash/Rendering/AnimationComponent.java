package com.ezra.supersmash.Rendering;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Map;

public class AnimationComponent {

    public enum HeroState {
        IDLE, ATTACKING, HURT, DEAD
    }

    private Map<HeroState, Animation<TextureRegion>> animations;
    private float stateTime = 0f;
    private HeroState currentState = HeroState.IDLE;

    public AnimationComponent(Map<HeroState, Animation<TextureRegion>> animations) {
        this.animations = animations;
    }

    public void update(float delta) {
        stateTime += delta;

        if (isCurrentAnimationFinished() && currentState != HeroState.IDLE && currentState != HeroState.DEAD) {
            setState(HeroState.IDLE);
        }
    }

    public TextureRegion getFrame() {
        Animation<TextureRegion> currentAnimation = animations.get(currentState);
        if (currentAnimation == null) {
            currentAnimation = animations.get(HeroState.IDLE);
        }
        boolean looping = (currentState == HeroState.IDLE);
        return currentAnimation.getKeyFrame(stateTime, looping);
    }

    public void setState(HeroState newState) {
        if (currentState == HeroState.DEAD && newState != HeroState.DEAD) return;

        if (this.currentState != newState) {
            this.currentState = newState;
            this.stateTime = 0f;
        }
    }

    public HeroState getCurrentState() {
        return currentState;
    }

    public boolean isCurrentAnimationFinished() {
        Animation<TextureRegion> currentAnimation = animations.get(currentState);
        if (currentAnimation != null) {
            return currentAnimation.isAnimationFinished(stateTime);
        }
        return true;
    }

    public static Animation<TextureRegion> createAnimation(String texturePath, int frameCols, float frameDuration) {
        Texture sheet = new Texture(texturePath);
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / frameCols, sheet.getHeight());
        TextureRegion[] frames = new TextureRegion[frameCols];
        System.arraycopy(tmp[0], 0, frames, 0, frameCols);
        return new Animation<>(frameDuration, frames);
    }
}
