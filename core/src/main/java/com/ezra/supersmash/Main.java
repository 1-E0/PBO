package com.ezra.supersmash;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        // SoundManager.load() has been removed
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
        // SoundManager.dispose() has been removed
    }
}
