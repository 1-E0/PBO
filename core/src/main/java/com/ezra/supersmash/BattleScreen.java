package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ezra.supersmash.Rendering.AnimationComponent;

import java.util.Random;

public class BattleScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Label player1Status, player2Status, player1EnergyLabel, player2EnergyLabel, turnLabel, logLabel;
    private Table actionTable;
    private static final int BASIC_ATTACK_COST = 1;
    private static final int SKILL_COST = 3;

    public BattleScreen(Main game, Player player1, Player player2) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        String[] backgroundPaths = {"backgrounds/game_background_1.png", "backgrounds/game_background_2.png", "backgrounds/game_background_3.png", "backgrounds/game_background_4.png"};
        Random random = new Random();
        String randomBackground = backgroundPaths[random.nextInt(backgroundPaths.length)];
        background = new Texture(Gdx.files.internal(randomBackground));
        this.currentPlayer = this.player1;
        setupUI();
        startNewTurn();
    }

    private void setupUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        turnLabel = new Label("", skin);
        logLabel = new Label("Battle Begins!", skin);
        Table p1Table = new Table();
        player1Status = new Label("", skin);
        player1Status.setWrap(true);
        player1EnergyLabel = new Label("", skin);
        p1Table.add(player1Status).width(300).row();
        p1Table.add(player1EnergyLabel).padTop(10);
        Table p2Table = new Table();
        player2Status = new Label("", skin);
        player2Status.setWrap(true);
        player2EnergyLabel = new Label("", skin);
        p2Table.add(player2Status).width(300).row();
        p2Table.add(player2EnergyLabel).padTop(10);
        actionTable = new Table();
        TextButton attackButton = new TextButton("Attack (1)", skin);
        TextButton skillButton = new TextButton("Skill (3)", skin);
        TextButton endTurnButton = new TextButton("End Turn", skin);
        actionTable.add(attackButton).pad(10).width(120).height(50);
        actionTable.add(skillButton).pad(10).width(120).height(50);
        actionTable.add(endTurnButton).pad(10).width(120).height(50);
        attackButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                handleAction(BASIC_ATTACK_COST, () -> {
                    Player opponent = (currentPlayer == player1) ? player2 : player1;
                    currentPlayer.getActiveHero().basicAttack(opponent.getActiveHero());
                    logLabel.setText(currentPlayer.getActiveHero().getName() + " attacks " + opponent.getActiveHero().getName());
                });
            }
        });
        skillButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                handleAction(SKILL_COST, () -> {
                    Player opponent = (currentPlayer == player1) ? player2 : player1;
                    currentPlayer.getActiveHero().useSkill(opponent.getActiveHero());
                    logLabel.setText(currentPlayer.getActiveHero().getName() + " used a skill on " + opponent.getActiveHero().getName());
                });
            }
        });
        endTurnButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                endTurn();
            }
        });
        Table topUITable = new Table();
        topUITable.add(p1Table).expandX().left().padLeft(20);
        topUITable.add(p2Table).expandX().right().padRight(20);
        root.add(turnLabel).colspan(2).pad(20).row();
        root.add(topUITable).growX().row();
        root.add(logLabel).colspan(2).pad(40).expandY().top().row();
        root.add(actionTable).colspan(2).padBottom(20);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        player1.getActiveHero().animationComponent.update(delta);
        player2.getActiveHero().animationComponent.update(delta);
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.getBatch().begin();
        TextureRegion p1Frame = player1.getActiveHero().animationComponent.getFrame();
        stage.getBatch().draw(p1Frame, 100, 150, p1Frame.getRegionWidth() * 4, p1Frame.getRegionHeight() * 4);
        TextureRegion p2Frame = player2.getActiveHero().animationComponent.getFrame();
        if (!p2Frame.isFlipX()) { p2Frame.flip(true, false); }
        stage.getBatch().draw(p2Frame, Gdx.graphics.getWidth() - 100 - (p2Frame.getRegionWidth() * 4), 150, p2Frame.getRegionWidth() * 4, p2Frame.getRegionHeight() * 4);
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();
    }

    private void handleAction(int cost, Runnable action) {
        if (currentPlayer.getActiveHero().animationComponent.getCurrentState() != AnimationComponent.HeroState.IDLE) return;
        if (currentPlayer.spendEnergy(cost)) {
            currentPlayer.getActiveHero().animationComponent.setState(AnimationComponent.HeroState.ATTACKING);
            action.run();
            updateUI();
            checkForDefeatedHero();
        } else {
            logLabel.setText("Not enough energy!");
        }
    }

    private void endTurn() {
        if (currentPlayer.getActiveHero().animationComponent.getCurrentState() != AnimationComponent.HeroState.IDLE) return;
        currentPlayer.getActiveHero().applyAndDecrementEffects();
        logLabel.setText(currentPlayer.getActiveHero().getName() + " ends their turn.");
        updateUI();
        checkForDefeatedHero();
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        startNewTurn();
    }

    private void checkForDefeatedHero() { if (player1.hasLost() || player2.hasLost()) endGame(); }
    private void endGame() { if (player1.hasLost()) game.setScreen(new GameOverScreen(game, "Player 2 Wins!")); else if (player2.hasLost()) game.setScreen(new GameOverScreen(game, "Player 1 Wins!")); }
    private void startNewTurn() { logLabel.setText("It's " + (currentPlayer == player1 ? "Player 1's" : "Player 2's") + " turn."); currentPlayer.gainEnergy(2); updateUI(); if (currentPlayer.getActiveHero().isStunned()) { logLabel.setText(logLabel.getText() + "\n" + currentPlayer.getActiveHero().getName() + " is stunned and cannot act!"); endTurn(); } }
    private void updateUI() { player1Status.setText(player1.getActiveHero().getStatus()); player2Status.setText(player2.getActiveHero().getStatus()); player1EnergyLabel.setText("Energy: " + player1.getEnergy()); player2EnergyLabel.setText("Energy: " + player2.getEnergy()); turnLabel.setText((currentPlayer == player1 ? "Player 1's" : "Player 2's") + " Turn"); }
    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); background.dispose(); skin.dispose(); }
}
