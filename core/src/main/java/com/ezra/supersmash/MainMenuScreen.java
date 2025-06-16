package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture background;
    private Music music;
    private Sound click;

    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture("backgrounds/menu_bg.png");

        click = Gdx.audio.newSound(Gdx.files.internal("sounds/hover.mp3"));

        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Mainmenu.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Label title = new Label("SUPER SMASH", skin);
        title.setFontScale(2f);
        TextButton play = new TextButton("Play", skin);
        TextButton quit = new TextButton("Quit", skin);
        table.center();
        table.add(title).padBottom(50).row();
        table.add(play).width(250).height(60).padBottom(20).row();
        table.add(quit).width(250).height(60);

        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                click.play();
                game.setScreen(new HeroSelectScreen(game));
            }
        });
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                click.play();
                Gdx.app.exit();
            }
        });
    }

    @Override public void show() {
        music.play();
    } // The SoundManager call was removed from here
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.getBatch().begin(); stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); stage.getBatch().end(); stage.act(delta); stage.draw();
    }
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void hide() {
        music.stop();
    }
    @Override public void pause() {

    }
    @Override public void resume() {

    }
    @Override public void dispose() {
        stage.dispose();
        background.dispose();
        music.dispose();
    }
}
