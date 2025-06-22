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

        // --- Perbaikan Penggunaan FreeTypeFontGenerator ---
        // Langkah 1: Buat instance generator
        // Pastikan Ancient_Medium.ttf ada di folder assets Anda
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Silver.ttf")); //

        // Langkah 2: Buat parameter font
        FreeTypeFontParameter parameter = new FreeTypeFontParameter(); //
        parameter.size = 60; // Ukuran font untuk judul, sesuaikan sesuai kebutuhan
        parameter.color = Color.WHITE; // Warna font
        parameter.borderWidth = 2; // Tebal border (opsional)
        parameter.borderColor = Color.DARK_GRAY; // Warna border (opsional)

        // Langkah 3: Hasilkan BitmapFont
        menuFont = fontGenerator.generateFont(parameter); //

        // Langkah 4: Tambahkan font yang dihasilkan ke Skin Anda
        // Beri nama gaya font ini, misalnya "menuFont" atau "AncientMedium"
        skin.add("menuFont", menuFont); //

        // Untuk tombol, kita bisa gunakan ukuran yang lebih kecil
        parameter.size = 30; // Ukuran font untuk tombol
        BitmapFont buttonFont = fontGenerator.generateFont(parameter); // Hasilkan font terpisah untuk tombol
        skin.add("buttonFont", buttonFont); // Tambahkan juga ke skin

        // Langkah 5: Buat TextButtonStyle baru untuk tombol jika Anda ingin kontrol penuh
        // atau Anda bisa langsung menggunakan font di gaya default TextButton jika uiskin.json Anda mengizinkannya
        // Contoh membuat TextButtonStyle baru
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

        // Gunakan font baru untuk judul
        //Label title = new Label("SUPER SMASH", skin, "menuFont", Color.WHITE); //
        //title.setFontScale(1f); // Karena ukuran sudah diatur di parameter.size, setScale ke 1f
        Image title = new Image(titleImage);

        // Gunakan gaya default atau gaya baru untuk tombol
        TextButton play = new TextButton("Play", skin); // Akan menggunakan "default" TextButtonStyle
        TextButton quit = new TextButton("Quit", skin); // Akan menggunakan "default" TextButtonStyle

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
        // --- Sangat penting: dispose generator dan font yang dihasilkan ---
        if (menuFont != null) { //
            menuFont.dispose(); //
        }
        if (fontGenerator != null) { //
            fontGenerator.dispose(); //
        }
        // Catatan: skin.dispose() biasanya juga akan dispose font yang ditambahkan ke dalamnya,
        // tapi mendispose font secara eksplisit akan lebih aman jika ada referensi lain.
        // Asumsi skin didispose di sini atau di tempat lain yang sesuai.
        // Jika Anda membuat 'buttonFont' terpisah, pastikan itu juga didispose.
        // Misalnya: if (skin != null) skin.dispose(); jika skin dibuat di level kelas
        // atau dispose buttonFont secara manual jika Anda menyimpannya sebagai field.
    }
}
