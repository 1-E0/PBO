package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.Fireball;

import java.util.HashMap;
import java.util.Map;

public class Mage extends Hero {
    private Sound fireball;
    public Mage() {
        super("Mage", 80, new Fireball(), createMageAnimations());
        fireball = Gdx.audio.newSound(Gdx.files.internal("sounds/fireball.mp3"));
    }

    private static AnimationComponent createMageAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();

        // Paths now point to the "characters" folder and use the new mage sprites.
        // Frame counts are estimated from the provided sprite sheets.
        animations.put(AnimationComponent.HeroState.IDLE, AnimationComponent.createAnimation("characters/mage_idle.png", 7, 0.1f)); //
        animations.put(AnimationComponent.HeroState.ATTACKING, AnimationComponent.createAnimation("characters/mage_attack.png", 8, 0.08f)); //
        animations.put(AnimationComponent.HeroState.HURT, AnimationComponent.createAnimation("characters/mage_hurt.png", 3, 0.1f)); //
        animations.put(AnimationComponent.HeroState.DEAD, AnimationComponent.createAnimation("characters/mage_dead.png", 6, 0.15f)); //

        return new AnimationComponent(animations);
    }

    @Override
    public void basicAttack(Hero target) {
        fireball.play(0.1f);
        System.out.println(name + " attacks with magic missile!");
        dealDamage(target, 15); // Gunakan metode publik yang diwariskan
    }
}
