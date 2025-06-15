package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Element;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.Fireball;

import java.util.HashMap;
import java.util.Map;

public class Mage extends Hero {
    public Mage() {
        super("Mage", 80, new Fireball(), createMageAnimations(), Element.FIRE);
    }

    private static AnimationComponent createMageAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();
        Animation<TextureRegion> idleAnimation = AnimationComponent.createAnimation("characters/mage.png", 1, 1f);

        animations.put(AnimationComponent.HeroState.IDLE, idleAnimation);
        animations.put(AnimationComponent.HeroState.ATTACKING, idleAnimation);
        animations.put(AnimationComponent.HeroState.HURT, idleAnimation);
        animations.put(AnimationComponent.HeroState.DEAD, idleAnimation);
        return new AnimationComponent(animations);
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " attacks with magic missile!");
        target.takeDamage(15);
    }
}
