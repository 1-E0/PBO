package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Element;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.HammerSwing;

import java.util.HashMap;
import java.util.Map;

public class Warrior extends Hero {
    private Sound swing;

    public Warrior() {
        super("Warrior", 120, new HammerSwing(), createWarriorAnimations(), Element.NEUTRAL);
        swing = Gdx.audio.newSound(Gdx.files.internal("sounds/swing.mp3"));

    }

    private static AnimationComponent createWarriorAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();

        // Paths now point to the "characters" folder and use the new warrior sprites
        // Assumed frame counts and durations based on common animation practices and Archer example
        animations.put(AnimationComponent.HeroState.IDLE, AnimationComponent.createAnimation("characters/warrior_idle.png", 4, 0.1f));
        animations.put(AnimationComponent.HeroState.ATTACKING, AnimationComponent.createAnimation("characters/warrior_attack.png", 5, 0.08f));
        animations.put(AnimationComponent.HeroState.HURT, AnimationComponent.createAnimation("characters/warrior_hurt.png", 2, 0.1f));
        animations.put(AnimationComponent.HeroState.DEAD, AnimationComponent.createAnimation("characters/warrior_dead.png", 6, 0.15f));

        return new AnimationComponent(animations);
    }

    @Override
    public void basicAttack(Hero target) {
        swing.play();
        System.out.println(name + " attacks with sword!");
        target.takeDamage(20);
    }
}
