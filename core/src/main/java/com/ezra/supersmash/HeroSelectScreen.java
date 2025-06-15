package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ezra.supersmash.Heroes.*;

public class HeroSelectScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private Integer player1Pick = null;
    private Integer player2Pick = null;
    private int currentPlayer = 1;
    private Label title;
    private TextButton confirmButton;

    public HeroSelectScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        background = new Texture("backgrounds/PlayerSelectionBackground.png");
        setupUI();
    }

    private void setupUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        title = new Label("Player 1: Select Your Hero", skin);
        title.setFontScale(1.5f);
        Table heroTable = new Table();
        String[] heroNames = {"Warrior", "Archer", "Mage", "Assassin", "Tank"};
        for (int i = 0; i < heroNames.length; i++) {
            final int heroIndex = i;
            TextButton heroButton = new TextButton(heroNames[i], skin);
            heroButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Sound call removed
                    if (currentPlayer == 1) player1Pick = heroIndex;
                    else player2Pick = heroIndex;
                    confirmButton.setDisabled(false);
                }
            });
            heroTable.add(heroButton).width(120).height(100).pad(10);
        }
        confirmButton = new TextButton("Confirm", skin);
        confirmButton.setDisabled(true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Sound call removed
                if (currentPlayer == 1) {
                    currentPlayer = 2;
                    title.setText("Player 2: Select Your Hero");
                    confirmButton.setDisabled(true);
                } else {
                    startGame();
                }
            }
        });
        root.top().padTop(40);
        root.add(title).padBottom(30).row();
        root.add(heroTable).padBottom(40).row();
        root.add(confirmButton).width(150);
    }

    private Hero getHeroByIndex(int index) {
        switch (index) {
            case 0: return new Warrior();
            case 1: return new Archer();
            case 2: return new Mage();
            case 3: return new Assassin();
            case 4: return new Tank();
            default: throw new IllegalArgumentException("Invalid hero index");
        }
    }

    private void startGame() {
        Player player1 = new Player(getHeroByIndex(player1Pick));
        Player player2 = new Player(getHeroByIndex(player2Pick));
        game.setScreen(new BattleScreen(game, player1, player2));
    }

    @Override public void render(float delta) { Gdx.gl.glClearColor(0, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
