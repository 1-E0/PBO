package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ezra.supersmash.Heroes.*;

import java.util.ArrayList;
import java.util.List;

public class HeroSelectScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private int currentPlayer = 1;
    private Label title;
    private TextButton confirmButton;
    private Sound hover;
    private Sound click;

    private Table tooltipTable;
    private Label tooltipLabel;
    private final HeroInfo[] heroInfoData;

    // UBAH BARU: Tambahkan Music, Generator, dan Font
    private Music music;
    private FreeTypeFontGenerator fontGenerator;
    private BitmapFont customFont;

    private List<Integer> player1Picks = new ArrayList<>();
    private List<Integer> player2Picks = new ArrayList<>();
    private List<Label> heroNameLabels = new ArrayList<>();

    private static final int HEROES_TO_PICK = 3;
    private final Hero[] availableHeroes = {new Warrior(), new Archer(), new Mage(), new Assassin(), new Tank()};

    private final float[] heroManualOffsetsX = { 15f, 0f, 10f, 5f, 10f };


    public HeroSelectScreen(Main game) {
        this.game = game;
        heroInfoData = new HeroInfo[]{
            // Warrior
            new HeroInfo("HP: 120\nDMG: 20", "Hammer Swing:\nMenyerang musuh dan memiliki 50% kemungkinan untuk menyebabkan Stun selama 1 giliran."),
            // Archer
            new HeroInfo("HP: 90\nDMG: 18", "Multi Shot:\nMenembak musuh, memberikan damage dan efek Bleed (damage dari waktu ke waktu)."),
            // Mage
            new HeroInfo("HP: 80\nDMG: 15", "Fireball:\nMenembakkan bola api yang memberikan damage dan efek Burn (damage dari waktu ke waktu)."),
            // Assassin
            new HeroInfo("HP: 70\nDMG: 25", "Shadow Strike:\nSerangan kuat yang mengabaikan defense dan membuat target rentan, menerima 50% damage tambahan dari serangan berikutnya."),
            // Tank
            new HeroInfo("HP: 160\nDMG: 10", "Shield Bash:\nSerangan lemah yang memprovokasi (Mock) musuh, sekaligus meningkatkan pertahanan diri sendiri dan menurunkan serangan target.")
        };

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        background = new Texture("backgrounds/PlayerSelectionBackground.png");
        hover = Gdx.audio.newSound(Gdx.files.internal("sounds/hoverbutton.mp3"));
        click = Gdx.audio.newSound(Gdx.files.internal("sounds/hover.mp3"));

        // UBAH BARU: Muat musik dari Main Menu
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Mainmenu.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);

        setupUI();
    }

    private void setupUI() {
        // UBAH BARU: Generate font kustom dari file .ttf
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Silver.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 28;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.5f;
        parameter.borderColor = new Color(0.2f, 0.2f, 0.2f, 1f);
        customFont = fontGenerator.generateFont(parameter);

        // Buat style baru untuk digunakan oleh elemen UI
        Label.LabelStyle labelStyle = new Label.LabelStyle(customFont, Color.WHITE);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(
            skin.getDrawable("button-normal"),
            skin.getDrawable("button-normal-pressed"),
            null,
            customFont);

        // Tambahkan style baru ke skin dengan nama yang unik
        skin.add("customLabelStyle", labelStyle);
        skin.add("customButtonStyle", buttonStyle);

        Stack mainStack = new Stack();
        mainStack.setFillParent(true);
        stage.addActor(mainStack);

        Table root = new Table();
        root.setFillParent(true);

        Table backButtonContainer = new Table();
        backButtonContainer.setFillParent(true);

        mainStack.add(root);
        mainStack.add(backButtonContainer);

        title = new Label("", skin);
        title.setFontScale(2.0f);

        Table heroTable = new Table();
        for (int i = 0; i < availableHeroes.length; i++) {
            final int heroIndex = i;
            Hero hero = availableHeroes[i];

            TextureRegion heroSprite = hero.animationComponent.getFrame();
            Image heroImage = new Image(heroSprite);

            // Gunakan style font yang baru dibuat
            Label heroNameLabel = new Label(hero.getName(), skin, "customLabelStyle");
            heroNameLabel.setAlignment(Align.center);
            heroNameLabels.add(heroNameLabel);

            Button heroButton = new Button(skin);
            Table contentTable = new Table();
            Container<Image> imageContainer = new Container<>(heroImage);
            imageContainer.padLeft(heroManualOffsetsX[i]);
            contentTable.add(imageContainer).size(120, 120).expandX().center().row();
            contentTable.add(heroNameLabel).padTop(10);
            contentTable.setTouchable(Touchable.disabled);
            heroButton.add(contentTable).expand().fill();
            heroButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) { handleHeroSelection(heroIndex); }
                @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1) {
                        hover.play(0.1f);

                        // --- Logika untuk menampilkan tooltip ---
                        HeroInfo info = heroInfoData[heroIndex];
                        String tooltipText = info.stats + "\n\nSkill: " + info.skillDesc;
                        tooltipLabel.setText(tooltipText);
                        tooltipTable.pack(); // Atur ukuran table agar pas dengan teks

                        // Posisikan tooltip di dekat kursor
                        float tooltipX = event.getStageX() + 15f;
                        float tooltipY = event.getStageY() - 15f - tooltipTable.getHeight();

                        // Cek agar tidak keluar layar
                        if (tooltipX + tooltipTable.getWidth() > stage.getWidth()) {
                            tooltipX = event.getStageX() - 15f - tooltipTable.getWidth();
                        }
                        if (tooltipY < 0) {
                            tooltipY = event.getStageY() + 15f;
                        }

                        tooltipTable.setPosition(tooltipX, tooltipY);
                        tooltipTable.setVisible(true);
                    }
                }
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (pointer == -1) {
                        tooltipTable.setVisible(false); // Sembunyikan tooltip
                    }
                }
            });
            heroTable.add(heroButton).width(180).height(200).pad(15);
        }

        // Gunakan style font yang baru dibuat
        confirmButton = new TextButton("Confirm", skin, "customButtonStyle");
        confirmButton.setDisabled(true);
        confirmButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { handleConfirmation(); }
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { if (pointer == -1 && !confirmButton.isDisabled()) { hover.play(0.1f); } }
        });

        // Gunakan style font yang baru dibuat
        TextButton backButton = new TextButton("Back", skin, "customButtonStyle");
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                click.play(0.5f);
                game.setScreen(new MainMenuScreen(game));
            }
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { if (pointer == -1) { hover.play(0.1f); } }
        });

        root.top().padTop(50);
        root.add(title).padBottom(40).row();
        root.add(heroTable).padBottom(50).row();
        root.add(confirmButton).width(250).height(70);

        backButtonContainer.bottom().left();
        backButtonContainer.add(backButton).width(150).height(60).pad(25f);

        tooltipTable = new Table(skin);
        tooltipTable.setBackground(skin.newDrawable("list", new Color(0.1f, 0.1f, 0.1f, 0.9f))); // Latar belakang semi-transparan
        tooltipTable.pad(10f);
        tooltipTable.setVisible(false); // Sembunyikan secara default

        tooltipLabel = new Label("", skin, "default");
        tooltipLabel.setWrap(true);
        tooltipLabel.setAlignment(Align.left);

        tooltipTable.add(tooltipLabel).width(220f); // Atur lebar agar teks bisa wrap
        stage.addActor(tooltipTable);

        updateTitle();
    }

    private void handleHeroSelection(int heroIndex) {
        click.play(0.5f);
        List<Integer> currentPicks = (currentPlayer == 1) ? player1Picks : player2Picks;

        if (currentPicks.contains(heroIndex)) {
            currentPicks.remove(Integer.valueOf(heroIndex));
        } else {
            if (currentPicks.size() < HEROES_TO_PICK) {
                currentPicks.add(heroIndex);
            }
        }

        // --- BLOK PERBAIKAN ---
        // Logika untuk mengubah warna highlight
        for(int i = 0; i < heroNameLabels.size(); i++) {
            Label label = heroNameLabels.get(i);

            // SEBELUMNYA (SALAH): label.getStyle().fontColor = Color.YELLOW;
            // SESUDAH (BENAR): Gunakan setColor() pada instance label
            if (currentPicks.contains(i)) {
                label.setColor(Color.YELLOW); // Mengubah warna label spesifik ini menjadi kuning
            } else {
                label.setColor(Color.WHITE); // Mengembalikan warna label spesifik ini ke putih
            }
        }
        confirmButton.setDisabled(currentPicks.size() != HEROES_TO_PICK);
        updateTitle();
    }

    @Override
    public void show() {
        // UBAH BARU: Mainkan musik saat layar ditampilkan
        music.play();
    }

    @Override
    public void hide() {
        // UBAH BARU: Hentikan musik saat layar disembunyikan
        music.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        background.dispose();
        click.dispose();
        hover.dispose();

        // UBAH BARU: Pastikan semua resource yang dibuat manual di-dispose
        music.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
        if (customFont != null) customFont.dispose();

        for (Hero hero : availableHeroes) {
            if (hero != null && hero.animationComponent != null && hero.animationComponent.getFrame() != null) {
                hero.animationComponent.getFrame().getTexture().dispose();
            }
        }
    }

    private void handleConfirmation() {
        List<Integer> currentPicks = (currentPlayer == 1) ? player1Picks : player2Picks;
        if (currentPicks.size() != HEROES_TO_PICK) return;

        if (currentPlayer == 1) {
            currentPlayer = 2;
            confirmButton.setDisabled(true);

            for(Label label : heroNameLabels) {
                label.setColor(Color.WHITE);
            }
            // --- AKHIR BLOK PERBAIKAN ---

            updateTitle();
        } else {
            startGame();
        }
    }

    private void updateTitle() {
        List<Integer> currentPicks = (currentPlayer == 1) ? player1Picks : player2Picks;
        title.setText(String.format("Player %d: Select %d Heroes (%d / %d)", currentPlayer, HEROES_TO_PICK, currentPicks.size(), HEROES_TO_PICK));
    }

    private Hero getHeroByIndex(int index) {
        switch (index) {
            case 0: return new Warrior();
            case 1: return new Archer();
            case 2: return new Mage();
            case 3: return new Assassin();
            case 4: return new Tank();
            default: throw new IllegalArgumentException("Invalid hero index: " + index);
        }
    }
    private void startGame() {
        if (player1Picks.size() != HEROES_TO_PICK || player2Picks.size() != HEROES_TO_PICK) {
            Gdx.app.error("START_GAME", "Attempted to start game with incomplete hero selection.");
            return;
        }

        List<Hero> player1HeroRoster = new ArrayList<>();
        for (int index : player1Picks) {
            player1HeroRoster.add(getHeroByIndex(index));
        }

        List<Hero> player2HeroRoster = new ArrayList<>();
        for (int index : player2Picks) {
            player2HeroRoster.add(getHeroByIndex(index));
        }

        Player player1 = new Player("Player 1", player1HeroRoster);
        Player player2 = new Player("Player 2", player2HeroRoster);

        game.setScreen(new BattleScreen(game, player1, player2));
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.getBatch().begin(); stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); stage.getBatch().end(); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}

    private static class HeroInfo {
        final String stats;
        final String skillDesc;

        HeroInfo(String stats, String skillDesc) {
            this.stats = stats;
            this.skillDesc = skillDesc;
        }
    }
}
