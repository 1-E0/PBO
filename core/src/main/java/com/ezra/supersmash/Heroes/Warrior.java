package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Element;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.HammerSwing;

import java.util.HashMap;
import java.util.Map;

public class Warrior extends Hero {
    public Warrior() {
        super("Warrior", 120, new HammerSwing(), createWarriorAnimations(), Element.NEUTRAL);
    }

    private static AnimationComponent createWarriorAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();
        // Using a single static image for now
        Animation<TextureRegion> idleAnimation = AnimationComponent.createAnimation("characters/warrior.png", 1, 1f);

        animations.put(AnimationComponent.HeroState.IDLE, idleAnimation);
        // Placeholder animations - they all point to the idle sprite
        animations.put(AnimationComponent.HeroState.ATTACKING, idleAnimation);
        animations.put(AnimationComponent.HeroState.HURT, idleAnimation);
        animations.put(AnimationComponent.HeroState.DEAD, idleAnimation);
        return new AnimationComponent(animations);
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " attacks with sword!");
        target.takeDamage(20);
    }
}
