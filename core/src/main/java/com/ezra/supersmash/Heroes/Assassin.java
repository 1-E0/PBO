package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.ShadowStrike;

import java.util.HashMap;
import java.util.Map;

public class Assassin extends Hero {
    public Assassin() {
        super("Assassin", 70, new ShadowStrike(), createAssassinAnimations());
    }

    private static AnimationComponent createAssassinAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();
        Animation<TextureRegion> idleAnimation = AnimationComponent.createAnimation("characters/assassin.png", 1, 1f);

        animations.put(AnimationComponent.HeroState.IDLE, idleAnimation);
        animations.put(AnimationComponent.HeroState.ATTACKING, idleAnimation);
        animations.put(AnimationComponent.HeroState.HURT, idleAnimation);
        animations.put(AnimationComponent.HeroState.DEAD, idleAnimation);
        return new AnimationComponent(animations);
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " slashes swiftly!");
        target.takeDamage(25);
    }
}
