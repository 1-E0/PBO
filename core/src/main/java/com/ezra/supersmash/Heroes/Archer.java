package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Element;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.MultiShot;

import java.util.HashMap;
import java.util.Map;

public class Archer extends Hero {
    public Archer() {
        super("Archer", 90, new MultiShot(), createArcherAnimations(), Element.NEUTRAL);
    }

    private static AnimationComponent createArcherAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();
        // Paths are now pointing to the "characters" folder
        animations.put(AnimationComponent.HeroState.IDLE, AnimationComponent.createAnimation("characters/archer_idle.png", 9, 0.1f));
        animations.put(AnimationComponent.HeroState.ATTACKING, AnimationComponent.createAnimation("characters/archer_attack.png", 14, 0.08f));
        animations.put(AnimationComponent.HeroState.HURT, AnimationComponent.createAnimation("characters/archer_hurt.png", 3, 0.1f));
        animations.put(AnimationComponent.HeroState.DEAD, AnimationComponent.createAnimation("characters/archer_dead.png", 5, 0.15f));
        return new AnimationComponent(animations);
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " shoots an arrow!");
        target.takeDamage(18);
    }
}
