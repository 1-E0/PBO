package com.ezra.supersmash.Heroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ezra.supersmash.Hero;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Skills.ShadowStrike;

import java.util.HashMap;
import java.util.Map;

public class Assassin extends Hero {
    private Sound dag;
    public Assassin() {
        super("Assassin", 70, new ShadowStrike(), createAssassinAnimations());
        dag = Gdx.audio.newSound(Gdx.files.internal("sounds/dagger.mp3"));
    }

    private static AnimationComponent createAssassinAnimations() {
        Map<AnimationComponent.HeroState, Animation<TextureRegion>> animations = new HashMap<>();

        animations.put(AnimationComponent.HeroState.IDLE, AnimationComponent.createAnimation("characters/assassin_idle.png", 9, 0.1f));
        animations.put(AnimationComponent.HeroState.ATTACKING, AnimationComponent.createAnimation("characters/assassin_attack.png", 6, 0.08f));
        animations.put(AnimationComponent.HeroState.HURT, AnimationComponent.createAnimation("characters/assassin_hurt.png", 2, 0.1f));
        animations.put(AnimationComponent.HeroState.DEAD, AnimationComponent.createAnimation("characters/assassin_dead.png", 5, 0.15f));

        return new AnimationComponent(animations);
    }

    @Override
    public void useSkill(Hero target) {
        dag.play(0.1f);
        super.useSkill(target);
    }

    @Override
    public void basicAttack(Hero target) {
        dag.play(0.1f);
        System.out.println(name + " slashes swiftly!");
        dealDamage(target, 25); // Gunakan metode publik yang diwariskan
    }
}
