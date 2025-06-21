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
    private final boolean isForDraft; // Flag untuk membedakan fase

    public ScrollActor(Scroll scroll, BattleScreen battleScreen, boolean isForDraft) {
        this.scroll = scroll;
        this.battleScreen = battleScreen;
        this.isForDraft = isForDraft;

        setSize(240, 330);
        setOrigin(Align.center);

        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // Efek hover hanya jika bisa di-klik
                if (isForDraft || battleScreen.isScrollUsable(scroll)) {
                    clearActions();
                    addAction(Actions.scaleTo(1.5f, 1.5f, 0.15f));
                }
                if (!isForDraft) {
                    battleScreen.showManualTooltip(scroll, event.getStageX(), event.getStageY());
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (isForDraft || battleScreen.isScrollUsable(scroll)) {
                    clearActions();
                    addAction(Actions.scaleTo(1f, 1f, 0.15f));
                }
                battleScreen.hideManualTooltip();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isForDraft) {
                    return true;
                }
                return battleScreen.isScrollUsable(scroll);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                battleScreen.hideManualTooltip();
                if (isForDraft) {
                    battleScreen.handleScrollDraftPick(scroll);
                } else {
                    // MODIFIKASI BARU
                    battleScreen.log("Select a target for " + scroll.getName());
                    battleScreen.onTargetSelected = (target) -> {
                        battleScreen.useScroll(scroll, target);
                    };
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor()); // Terapkan warna aktor (misal untuk fade out)
        batch.draw(scroll.getTexture(), getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public Scroll getScroll() {
        return scroll;
    }
}
