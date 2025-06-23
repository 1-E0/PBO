package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color; // Import Color untuk mengatur warna font
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Import BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter; // Import FreeTypeFontParameter
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture background;
    private Texture titleImage;
    private Music music;
    private Sound click;
    private Sound hover;

    // Tambahkan deklarasi untuk FreeTypeFontGenerator dan BitmapFont
    private FreeTypeFontGenerator fontGenerator; //
    private BitmapFont menuFont; //

    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture("backgrounds/menu_bg.png");
        titleImage = new Texture(Gdx.files.internal("backgrounds/SUPER-SMASH.png"));

        click = Gdx.audio.newSound(Gdx.files.internal("sounds/hover.mp3"));
        hover = Gdx.audio.newSound(Gdx.files.internal("sounds/hoverbutton.mp3"));

        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Mainmenu.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));


        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Silver.ttf")); //


        FreeTypeFontParameter parameter = new FreeTypeFontParameter(); //
        parameter.size = 60;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.DARK_GRAY;


        menuFont = fontGenerator.generateFont(parameter); //


        skin.add("menuFont", menuFont); //


        parameter.size = 30;
        BitmapFont buttonFont = fontGenerator.generateFont(parameter); // Hasilkan font terpisah untuk tombol
        skin.add("buttonFont", buttonFont); // Tambahkan juga ke skin


        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = buttonFont; // Gunakan font yang baru dibuat
        textButtonStyle.up = skin.getDrawable("button-normal");
        textButtonStyle.down = skin.getDrawable("button-normal-pressed");
        textButtonStyle.over = skin.getDrawable("button-normal-over");
        textButtonStyle.fontColor = Color.WHITE; // Atur warna font untuk tombol
        skin.add("default", textButtonStyle); // Menimpa gaya default atau beri nama baru jika Anda punya gaya lain

        // ----------------------------------------------------

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);


        Image title = new Image(titleImage);


        TextButton play = new TextButton("Play", skin);
        TextButton quit = new TextButton("Quit", skin);

        table.center();
        table.add(title).padBottom(200).row();
        table.add(play).width(250).height(60).padBottom(20).row();
        table.add(quit).width(250).height(60);

        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                click.play();
                game.setScreen(new HeroSelectScreen(game));
            }
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    hover.play(0.1f);
                }
            }
        });
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                click.play();
                Gdx.app.exit();
            }
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    hover.play(0.1f);
                }
            }
        });
    }

    @Override public void show() {
        music.play();
    }
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
    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
        music.dispose();

        if (menuFont != null) { //
            menuFont.dispose(); //
        }
        if (fontGenerator != null) { //
            fontGenerator.dispose(); //
        }

    }
}
