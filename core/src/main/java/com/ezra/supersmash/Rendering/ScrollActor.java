package com.ezra.supersmash.Rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Scroll;

// VERSI FINAL - HANYA ANIMASI SKALA
public class ScrollActor extends Actor {
    private final Scroll scroll;
    private final BattleScreen battleScreen;
    private final boolean canUse;

    public ScrollActor(Scroll scroll, BattleScreen battleScreen, Skin skin, boolean canUse) {
        this.scroll = scroll;
        this.battleScreen = battleScreen;
        // this.skin = skin; // Skin tidak lagi digunakan di constructor ini
        this.canUse = canUse;

        setSize(80, 110);
        setOrigin(Align.center);

        final TextTooltip tooltip = new TextTooltip(scroll.getDescription(), skin);
        tooltip.setInstant(true);
        addListener(tooltip);

        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (canUse) {
                    clearActions();
                    addAction(Actions.scaleTo(2f, 2f, 0.15f));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (canUse) {
                    clearActions();
                    addAction(Actions.scaleTo(1.0f, 1.0f, 0.15f));
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (canUse) {
                    battleScreen.useScroll(scroll);
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Metode draw ini SAMA SEKALI tidak menyentuh warna batch.
        batch.draw(scroll.getTexture(), getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
