package com.ezra.supersmash;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }

    }
}
