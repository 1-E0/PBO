package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    private List<Integer> player1Picks = new ArrayList<>();
    private List<Integer> player2Picks = new ArrayList<>();
    private List<TextButton> heroButtons = new ArrayList<>();

    private static final int HEROES_TO_PICK = 3;

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

        title = new Label("", skin);
        title.setFontScale(1.5f);

        Table heroTable = new Table();
        String[] heroNames = {"Warrior", "Archer", "Mage", "Assassin", "Tank"};
        for (int i = 0; i < heroNames.length; i++) {
            final int heroIndex = i;
            TextButton heroButton = new TextButton(heroNames[i], skin);
            heroButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleHeroSelection(heroIndex);
                }
            });
            heroButtons.add(heroButton);
            heroTable.add(heroButton).width(120).height(100).pad(10);
        }

        confirmButton = new TextButton("Confirm", skin);
        confirmButton.setDisabled(true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleConfirmation();
            }
        });

        root.top().padTop(40);
        root.add(title).padBottom(30).row();
        root.add(heroTable).padBottom(40).row();
        root.add(confirmButton).width(150);

        updateTitle();
    }

    private void handleHeroSelection(int heroIndex) {
        List<Integer> currentPicks = (currentPlayer == 1) ? player1Picks : player2Picks;

        if (currentPicks.contains(heroIndex)) {
            currentPicks.remove(Integer.valueOf(heroIndex));
        } else {
            if (currentPicks.size() < HEROES_TO_PICK) {
                currentPicks.add(heroIndex);
            }
        }

        // Perbarui tampilan visual semua tombol
        for(int i = 0; i < heroButtons.size(); i++) {
            TextButton btn = heroButtons.get(i);
            // Tombol "dicentang" jika indeksnya ada di dalam daftar pilihan
            if (currentPicks.contains(i)) {
                btn.getLabel().setColor(Color.YELLOW);
            } else {
                btn.getLabel().setColor(Color.WHITE);
            }
        }

        confirmButton.setDisabled(currentPicks.size() != HEROES_TO_PICK);
        updateTitle();
    }

    private void handleConfirmation() {
        List<Integer> currentPicks = (currentPlayer == 1) ? player1Picks : player2Picks;
        if (currentPicks.size() != HEROES_TO_PICK) return; // Pengaman tambahan

        if (currentPlayer == 1) {
            currentPlayer = 2;
            confirmButton.setDisabled(true);

            // Reset visual tombol untuk pemain berikutnya
            for(TextButton btn : heroButtons) {
                btn.getLabel().setColor(Color.WHITE);
            }
            updateTitle();
        } else {
            startGame();
        }
    }

    private void updateTitle() {
        List<Integer> currentPicks = (currentPlayer == 1) ? player1Picks : player2Picks;
        title.setText(String.format("Player %d: Select Hero (%d / %d)", currentPlayer, currentPicks.size(), HEROES_TO_PICK));
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
    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); background.dispose(); }
}
