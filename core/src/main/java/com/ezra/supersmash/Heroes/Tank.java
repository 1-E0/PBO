package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.ShieldBash;

import java.util.HashMap;
import java.util.Map;

public class Tank extends Hero {
    private Sound axe;
    public Tank() {
        super("Tank", 160, new ShieldBash(), createTankAnimations());
        axe = Gdx.audio.newSound(Gdx.files.internal("sounds/axe.mp3"));
    }

    private static AnimationComponent createTankAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();

        animations.put(AnimationComponent.HeroState.IDLE, AnimationComponent.createAnimation("characters/tank_idle.png", 10, 0.1f)); //
        animations.put(AnimationComponent.HeroState.ATTACKING, AnimationComponent.createAnimation("characters/tank_attack.png", 5, 0.08f)); //
        animations.put(AnimationComponent.HeroState.HURT, AnimationComponent.createAnimation("characters/tank_hurt.png", 3, 0.1f)); //
        animations.put(AnimationComponent.HeroState.DEAD, AnimationComponent.createAnimation("characters/tank_dead.png", 5, 0.15f)); //

        return new AnimationComponent(animations);
    }

    @Override
    public void useSkill(Hero target) {
        axe.play(0.1f);
        super.useSkill(target);
    }

    @Override
    public void basicAttack(Hero target) {
        axe.play(0.1f);
        System.out.println(name + " charges forward!");
        dealDamage(target, 10);
    }
}
