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
        super("Tank", 160, new ShieldBash(), createTankAnimations(), Element.NATURE);
    }

    private static AnimationComponent createTankAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();

        // Paths now point to the "characters" folder and use the new tank sprites.
        // Frame counts are estimated from the provided sprite sheets.
        animations.put(AnimationComponent.HeroState.IDLE, AnimationComponent.createAnimation("characters/tank_idle.png", 10, 0.1f)); //
        animations.put(AnimationComponent.HeroState.ATTACKING, AnimationComponent.createAnimation("characters/tank_attack.png", 5, 0.08f)); //
        animations.put(AnimationComponent.HeroState.HURT, AnimationComponent.createAnimation("characters/tank_hurt.png", 3, 0.1f)); //
        animations.put(AnimationComponent.HeroState.DEAD, AnimationComponent.createAnimation("characters/tank_dead.png", 5, 0.15f)); //

        return new AnimationComponent(animations);
    }

    @Override
    public void basicAttack(Hero target) {
        System.out.println(name + " charges forward!");
        target.takeDamage(10);
    }
}
