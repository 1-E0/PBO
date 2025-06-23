package com.ezra.supersmash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FloatingText extends Label {

    public FloatingText(String text, Skin skin, Color color) {
        super(text, skin);
        this.setFontScale(1.2f);
        this.setColor(color);
    }


    public void animate() {

        addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0, 70, 1.5f),
                    Actions.fadeOut(1.5f)
                ),
                Actions.removeActor()
            )
        );
    }
}
