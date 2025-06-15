package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Rendering.HeroActor;

import java.util.Random;
import java.util.function.Consumer;

public class BattleScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private Player player1, player2, currentPlayer, opponent;

    private Label turnLabel, logLabel;
    private Label[] p1HeroLabels = new Label[3];
    private Label[] p2HeroLabels = new Label[3];
    private HeroActor[] p1HeroActors = new HeroActor[3];
    private HeroActor[] p2HeroActors = new HeroActor[3];
    private TextButton attackButton, skillButton, endTurnButton;

    private enum BattleState { AWAITING_INPUT, PROCESSING }
    private BattleState currentState;
    private Consumer<Hero> onTargetSelected;

    public BattleScreen(Main game, Player player1, Player player2) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        background = new Texture(Gdx.files.internal(new String[]{"backgrounds/game_background_1.png", "backgrounds/game_background_2.png", "backgrounds/game_background_3.png", "backgrounds/game_background_4.png"}[new Random().nextInt(4)]));

        setupUI();
        startNewGame();
    }

    private void setupUI() {
        stage.clear();
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float yTop = screenHeight * 0.55f;
        float yMiddle = screenHeight * 0.40f;
        float yBottom = screenHeight * 0.25f;
        float[] yPositions = {yTop, yMiddle, yBottom};

        // Adjusted X positions to bring heroes even closer to the center
        float xP1_Back = screenWidth * 0.30f;
        float xP1_Front = screenWidth * 0.35f;
        float xP2_Back = screenWidth * 0.70f;
        float xP2_Front = screenWidth * 0.65f;

        float[] xPositionsP1 = {xP1_Back, xP1_Front, xP1_Back};
        float[] xPositionsP2 = {xP2_Back, xP2_Front, xP2_Back};

        float scaleTopBottom = 1.0f;
        float scaleMiddle = 1.2f;
        float[] scales = {scaleTopBottom, scaleMiddle, scaleTopBottom};
        float baseCharHeight = screenHeight / 6.5f;

        // Drawable for status box background
        Drawable statusBoxBg = skin.newDrawable("white", new Color(0, 0, 0, 0.6f));
        float statusBoxWidth = 160f;

        // Horizontal offsets for status UI based on side, kept as before as user did not specify change here
        float horizontalOffsetP1Status = 80f;
        float horizontalOffsetP2Status = 110f;

        for (int i = 0; i < 3; i++) {
            float charHeight = baseCharHeight * scales[i];

            // --- Player 1: Hero dan Kotak Statusnya ---
            Hero p1Hero = player1.getHeroRoster().get(i);
            p1HeroActors[i] = new HeroActor(p1Hero, false);
            float p1CharWidth = charHeight * getAspectRatio(p1Hero);
            p1HeroActors[i].setSize(p1CharWidth, charHeight);
            p1HeroActors[i].setPosition(xPositionsP1[i] - (p1CharWidth / 2), yPositions[i]);

            Table p1StatusBox = new Table();
            p1StatusBox.setBackground(statusBoxBg);
            p1HeroLabels[i] = new Label("-", skin);
            p1HeroLabels[i].setWrap(true);
            p1StatusBox.add(p1HeroLabels[i]).width(statusBoxWidth - 10).pad(5);
            p1StatusBox.pack(); // Ensure table size is set before positioning

            // Positioning the status box behind Player 1's hero, horizontally adjusted with P1-specific offset
            // Vertically, position it slightly lower than the hero's vertical center to be more "behind"
            p1StatusBox.setPosition(p1HeroActors[i].getX() + (p1CharWidth / 2) - (p1StatusBox.getWidth() / 2) - horizontalOffsetP1Status, p1HeroActors[i].getY() + (charHeight * 0.3f));
            stage.addActor(p1StatusBox); // Add status box first so it's drawn underneath

            stage.addActor(p1HeroActors[i]); // Add HeroActor after the status box, making it appear in front
            addHeroClickListener(p1HeroActors[i]);

            // --- Player 2: Hero dan Kotak Statusnya ---
            Hero p2Hero = player2.getHeroRoster().get(i);
            p2HeroActors[i] = new HeroActor(p2Hero, true);
            float p2CharWidth = charHeight * getAspectRatio(p2Hero);
            p2HeroActors[i].setSize(p2CharWidth, charHeight);
            p2HeroActors[i].setPosition(xPositionsP2[i] - (p2CharWidth / 2), yPositions[i]);

            Table p2StatusBox = new Table();
            p2StatusBox.setBackground(statusBoxBg);
            p2HeroLabels[i] = new Label("-", skin);
            p2HeroLabels[i].setWrap(true);
            p2StatusBox.add(p2HeroLabels[i]).width(statusBoxWidth - 10).pad(5);
            p2StatusBox.pack(); // Ensure table size is set before positioning

            // Positioning the status box behind Player 2's hero, horizontally adjusted with P2-specific offset
            // Vertically, position it slightly lower than the hero's vertical center to be more "behind"
            p2StatusBox.setPosition(p2HeroActors[i].getX() + (p2CharWidth / 2) - (p2StatusBox.getWidth() / 2) + horizontalOffsetP2Status, p2HeroActors[i].getY() + (charHeight * 0.3f));
            stage.addActor(p2StatusBox); // Add status box first so it's drawn underneath

            stage.addActor(p2HeroActors[i]); // Add HeroActor after the status box, making it appear in front
            addHeroClickListener(p2HeroActors[i]);
        }

        // --- Top and Bottom UI Panels ---
        Table topUiPanel = new Table();
        turnLabel = new Label("", skin);
        logLabel = new Label("", skin);
        logLabel.setWrap(true);
        logLabel.setAlignment(Align.center);
        topUiPanel.add(turnLabel).pad(10).row();
        topUiPanel.add(logLabel).width(screenWidth * 0.4f).row();
        root.add(topUiPanel).expand().top().padTop(screenHeight * 0.05f).row();

        Table actionTable = new Table();
        actionTable.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.5f)));
        attackButton = new TextButton("Attack (1)", skin);
        skillButton = new TextButton("Skill (3)", skin);
        endTurnButton = new TextButton("End Turn", skin);
        actionTable.add(attackButton).pad(10);
        actionTable.add(skillButton).pad(10);
        actionTable.add(endTurnButton).pad(10);
        addActionListeners();

        root.add(new Table()).expandY().row(); // Spacer to push the bottom panel down
        root.add(actionTable).padBottom(10).bottom();
    }

    private void addHeroClickListener(HeroActor actor) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentState != BattleState.AWAITING_INPUT) return;
                Hero clickedHero = actor.getHero();
                if (currentPlayer.getHeroRoster().contains(clickedHero) && clickedHero.isAlive()) {
                    currentPlayer.setActiveHero(currentPlayer.getHeroRoster().indexOf(clickedHero));
                    logLabel.setText(clickedHero.getName() + " is active! Select an action or target.");
                } else if (currentPlayer.getActiveHero() != null && opponent.getHeroRoster().contains(clickedHero) && clickedHero.isAlive()) {
                    if (onTargetSelected != null) {
                        onTargetSelected.accept(clickedHero);
                    }
                }
            }
        });
    }

    private void addActionListeners() {
        attackButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (attackButton.isDisabled()) return;
                Hero activeHero = currentPlayer.getActiveHero();
                if (activeHero.getEnergy() < 1) { logLabel.setText("Not enough energy!"); return; }
                logLabel.setText("ATTACK: Select an enemy target.");
                onTargetSelected = (target) -> {
                    activeHero.spendEnergy(1);
                    executeAction(activeHero, target, () -> {
                        activeHero.basicAttack(target);
                        logLabel.setText(activeHero.getName() + " attacks " + target.getName() + "!");
                    });
                };
            }
        });

        skillButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (skillButton.isDisabled()) return;
                Hero activeHero = currentPlayer.getActiveHero();
                if (activeHero.getEnergy() < 3) { logLabel.setText("Not enough energy!"); return; }
                logLabel.setText("SKILL: Select an enemy target.");
                onTargetSelected = (target) -> {
                    activeHero.spendEnergy(3);
                    executeAction(activeHero, target, () -> activeHero.useSkill(target));
                };
            }
        });

        endTurnButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (endTurnButton.isDisabled()) return;
                endTurn();
            }
        });
    }

    private void executeAction(Hero attacker, Hero target, Runnable actionLogic) {
        currentState = BattleState.PROCESSING;
        onTargetSelected = null;

        attacker.animationComponent.setState(AnimationComponent.HeroState.ATTACKING);
        actionLogic.run();

        float animationDuration = 1.3f;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                attacker.animationComponent.setState(AnimationComponent.HeroState.IDLE);
                endTurn();
            }
        }, animationDuration);
    }

    private float getAspectRatio(Hero hero) {
        return (float)hero.animationComponent.getFrame().getRegionWidth() / (float)hero.animationComponent.getFrame().getRegionHeight();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateUI();
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();
    }

    private void startNewGame() {
        currentPlayer = player1;
        opponent = player2;
        startNewTurn();
    }

    private void startNewTurn() {
        currentState = BattleState.PROCESSING;
        onTargetSelected = null;

        for (Hero hero : currentPlayer.getHeroRoster()) {
            if (hero.isAlive()) {
                hero.gainEnergy(1);
            }
        }

        currentPlayer.setActiveHero(-1);
        logLabel.setText(currentPlayer.getName() + "'s turn. Select your character.");
        currentState = BattleState.AWAITING_INPUT;
    }

    private void endTurn() {
        if(checkForDefeatedHero()) return;

        if (currentPlayer.getActiveHero() != null) {
            currentPlayer.getActiveHero().applyAndDecrementEffects();
        }
        if(checkForDefeatedHero()) return;

        Player temp = currentPlayer;
        currentPlayer = opponent;
        opponent = temp;

        startNewTurn();
    }

    private boolean checkForDefeatedHero() {
        if (player1.hasLost() || player2.hasLost()) {
            endGame();
            return true;
        }
        return false;
    }

    private void endGame() {
        currentState = BattleState.PROCESSING;
        game.setScreen(new GameOverScreen(game, (player1.hasLost() ? "Player 2 Wins!" : "Player 1 Wins!")));
    }

    private void updateUI() {
        turnLabel.setText(currentPlayer.getName() + "'s Turn");

        for(int i = 0; i < 3; i++) {
            p1HeroLabels[i].setText(player1.getHeroRoster().get(i).getStatus());
            p2HeroLabels[i].setText(player2.getHeroRoster().get(i).getStatus());

            p1HeroLabels[i].getParent().setColor(player1.getHeroRoster().get(i) == player1.getActiveHero() ? Color.GOLD : Color.WHITE);
            p2HeroLabels[i].getParent().setColor(player2.getHeroRoster().get(i) == player2.getActiveHero() ? Color.GOLD : Color.WHITE);
        }

        boolean canAct = currentState == BattleState.AWAITING_INPUT && currentPlayer.getActiveHero() != null;
        attackButton.setDisabled(!canAct);
        skillButton.setDisabled(!canAct);
        endTurnButton.setDisabled(currentState != BattleState.AWAITING_INPUT);
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); setupUI(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); background.dispose(); skin.dispose(); }
}
