package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HeroSelectScreen implements Screen {

    private final Main game;
    private final int playerNumber;
    private final Integer heroTaken; // digunakan jika ini Player 2

    private Stage stage;
    private Integer selectedHero = null;
    private Texture background;
    public HeroSelectScreen(Main game, int playerNumber, Integer heroTaken) {
        this.game = game;
        this.playerNumber = playerNumber;
        this.heroTaken = heroTaken;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture("backgrounds/PlayerSelectionBackground.png");
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("Select Hero - Player " + playerNumber, skin);
        title.setFontScale(1.5f);

        Table heroTable = new Table();
        for (int i = 0; i < 5; i++) {
            final int heroIndex = i;
            String buttonText = "Hero " + (i + 1);
            TextButton heroButton = new TextButton(buttonText, skin);
            heroButton.getLabel().setFontScale(1.2f);

            if (heroTaken != null && heroTaken == i + 1) {
                heroButton.setDisabled(true); // disable hero yang dipilih player 1
            }

            heroButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedHero = heroIndex + 1;
                    System.out.println("Player " + playerNumber + " selected Hero " + selectedHero);
                }
            });

            heroTable.add(heroButton).width(100).height(100).pad(10);
        }

        TextButton backButton = new TextButton("Back", skin);
        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.setDisabled(true);

        // Update confirm status
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                confirmButton.setDisabled(selectedHero == null);
                return false;
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playerNumber == 1) {
                    game.setScreen(new MainMenuScreen(game));
                } else {
                    game.setScreen(new HeroSelectScreen(game, 1, null)); // Kembali ke Player 1
                }
            }
        });

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedHero != null) {
                    if (playerNumber == 1) {
                        game.setScreen(new HeroSelectScreen(game, 2, selectedHero)); // lanjut ke player 2
                    } else {
                        System.out.println("Player 1 Hero: " + heroTaken);
                        System.out.println("Player 2 Hero: " + selectedHero);
                        // Lanjut ke game atau BattleScreen:
                        // game.setScreen(new BattleScreen(game, heroTaken, selectedHero));
                    }
                }
            }
        });

        // Layout
        root.top().padTop(40);
        root.add(title).colspan(3).padBottom(30).row();
        root.add(heroTable).colspan(3).padBottom(40).row();
        root.add(backButton).width(120).padRight(20);
        root.add(); // spacer
        root.add(confirmButton).width(120).padLeft(20);
    }

    @Override public void show() {}
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Gambar background dulu
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
