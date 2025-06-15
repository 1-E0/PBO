package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.ShieldBash;

import java.util.HashMap;
import java.util.Map;

public class Tank extends Hero {
    public Tank() {
        super("Tank", 160, new ShieldBash(), createTankAnimations());
    }

    private static AnimationComponent createTankAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();
        Animation<TextureRegion> idleAnimation = AnimationComponent.createAnimation("characters/tank.png", 1, 1f);

        animations.put(AnimationComponent.HeroState.IDLE, idleAnimation);
        animations.put(AnimationComponent.HeroState.ATTACKING, idleAnimation);
        animations.put(AnimationComponent.HeroState.HURT, idleAnimation);
        animations.put(AnimationComponent.HeroState.DEAD, idleAnimation);
        return new AnimationComponent(animations);
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " charges forward!");
        target.takeDamage(10);
    }
}
