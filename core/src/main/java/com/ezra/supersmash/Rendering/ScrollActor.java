package com.ezra.supersmash.Rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ezra.supersmash.BattleScreen;
import com.ezra.supersmash.Scroll;

public class ScrollActor extends Actor {
    private final Scroll scroll;
    private final BattleScreen battleScreen;
    private final boolean canUse;

    public ScrollActor(Scroll scroll, BattleScreen battleScreen, Skin skin, boolean canUse) {
        this.scroll = scroll;
        this.battleScreen = battleScreen;
        this.canUse = canUse;

        setSize(80, 110);
        setOrigin(Align.center);

        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (canUse) {
                    clearActions();
                    addAction(Actions.scaleTo(2.2f, 2.2f, 0.15f));
                }
                battleScreen.showManualTooltip(scroll, event.getStageX(), event.getStageY());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (canUse) {
                    clearActions();
                    addAction(Actions.scaleTo(1.0f, 1.0f, 0.15f));
                }
                battleScreen.hideManualTooltip();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Hanya proses klik jika 'canUse' bernilai true
                return canUse;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Logika penggunaan scroll dipindahkan ke sini
                battleScreen.hideManualTooltip();
                battleScreen.useScroll(scroll);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(scroll.getTexture(), getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
