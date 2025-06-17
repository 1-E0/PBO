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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ezra.supersmash.Effects.VulnerableEffect;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Rendering.HeroActor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class BattleScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private Player player1, player2, currentPlayer, opponent;

    private Label turnLabel, logLabel, turnCounterLabel;
    private int turnCount;
    private Table[] p1StatusTables = new Table[3];
    private Table[] p2StatusTables = new Table[3];
    private HeroActor[] p1HeroActors = new HeroActor[3];
    private HeroActor[] p2HeroActors = new HeroActor[3];
    private TextButton attackButton, skillButton, endTurnButton;
    private ProgressBar.ProgressBarStyle progressBarStyle;

    private Map<String, Texture> statusEffectIcons;


    private enum BattleState { AWAITING_INPUT, PROCESSING }
    private BattleState currentState;
    private Consumer<Hero> onTargetSelected;
    private boolean actionWasTaken = false;

    public BattleScreen(Main game, Player player1, Player player2) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        background = new Texture(Gdx.files.internal(new String[]{"backgrounds/game_background_1.png", "backgrounds/game_background_2.png", "backgrounds/game_background_3.png", "backgrounds/game_background_4.png"}[new Random().nextInt(4)]));

        progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);

        statusEffectIcons = new HashMap<>();
        loadStatusEffectIcons();

        setupUI();
        startNewGame();
    }

    private void loadStatusEffectIcons() {
        try {
            statusEffectIcons.put("Burn", new Texture("icons/burning.png"));
            statusEffectIcons.put("Bleed", new Texture("icons/bleeding.png"));
            statusEffectIcons.put("Stunned", new Texture("icons/Stun.png"));
            statusEffectIcons.put("Vulnerable", new Texture("icons/Vulnerable.png"));
            statusEffectIcons.put("Defense Up", new Texture("icons/defenseup.png"));
            statusEffectIcons.put("Attack Down", new Texture("icons/attackdown.png"));
        } catch (Exception e) {
            System.err.println("Gagal memuat ikon status effect. Pastikan file gambar ada di folder 'assets/icons/'.");
            e.printStackTrace();
        }
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

        Drawable statusBoxBg = skin.newDrawable("rect", new Color(0.2f, 0.2f, 0.2f, 0.5f));
        float statusBoxWidth = 160f;
        float statusBoxHeight = 100f;

        float horizontalOffsetP1Status = 160f;
        float horizontalOffsetP2Status = 180f;

        for (int i = 0; i < 3; i++) {
            float charHeight = baseCharHeight * scales[i];

            Hero p1Hero = player1.getHeroRoster().get(i);
            p1HeroActors[i] = new HeroActor(p1Hero, false);
            float p1CharWidth = charHeight * getAspectRatio(p1Hero);
            p1HeroActors[i].setSize(p1CharWidth, charHeight);
            p1HeroActors[i].setPosition(xPositionsP1[i] - (p1CharWidth / 2), yPositions[i]);

            p1StatusTables[i] = new Table();
            p1StatusTables[i].setBackground(statusBoxBg);
            p1StatusTables[i].setSize(statusBoxWidth, statusBoxHeight);
            p1StatusTables[i].setPosition(
                p1HeroActors[i].getX() + (p1CharWidth / 2) - (p1StatusTables[i].getWidth() / 2) - horizontalOffsetP1Status,
                p1HeroActors[i].getY() - 20f
            );
            stage.addActor(p1StatusTables[i]);
            stage.addActor(p1HeroActors[i]);
            addHeroClickListener(p1HeroActors[i]);

            Hero p2Hero = player2.getHeroRoster().get(i);
            p2HeroActors[i] = new HeroActor(p2Hero, true);
            float p2CharWidth = charHeight * getAspectRatio(p2Hero);
            p2HeroActors[i].setSize(p2CharWidth, charHeight);
            p2HeroActors[i].setPosition(xPositionsP2[i] - (p2CharWidth / 2), yPositions[i]);

            p2StatusTables[i] = new Table();
            p2StatusTables[i].setBackground(statusBoxBg);
            p2StatusTables[i].setSize(statusBoxWidth, statusBoxHeight);
            p2StatusTables[i].setPosition(
                p2HeroActors[i].getX() + (p2CharWidth / 2) - (p2StatusTables[i].getWidth() / 2) + horizontalOffsetP2Status,
                p2HeroActors[i].getY() - 20f
            );
            stage.addActor(p2StatusTables[i]);
            stage.addActor(p2HeroActors[i]);
            addHeroClickListener(p2HeroActors[i]);
        }

        Stack topUiStack = new Stack();

        Table topLeftContainer = new Table();
        topLeftContainer.top().left();
        turnCounterLabel = new Label("", skin, "window");
        turnCounterLabel.setFontScale(1.5f);
        topLeftContainer.add(turnCounterLabel).pad(20f);

        Table topCenterContainer = new Table();
        topCenterContainer.top();
        topCenterContainer.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.5f)));
        turnLabel = new Label("", skin, "highlighted");
        turnLabel.setFontScale(1.2f);
        logLabel = new Label("", skin, "highlighted");
        logLabel.setWrap(true);
        logLabel.setAlignment(Align.center);
        topCenterContainer.add(turnLabel).pad(10).row();
        topCenterContainer.add(logLabel).width(screenWidth * 0.4f).row();

        topUiStack.add(topCenterContainer);
        topUiStack.add(topLeftContainer);

        root.add(topUiStack).expandX().fillX().top().row();

        Table actionTable = new Table();
        actionTable.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.5f)));
        attackButton = new TextButton("Attack (1)", skin);
        skillButton = new TextButton("Skill (3)", skin);
        endTurnButton = new TextButton("End Turn", skin);
        actionTable.add(attackButton).pad(10);
        actionTable.add(skillButton).pad(10);
        actionTable.add(endTurnButton).pad(10);
        addActionListeners();

        root.add(new Table()).expandY().row();
        root.add(actionTable).padBottom(10).bottom();
    }

    private void populateStatusBox(Table box, Hero hero) {
        box.clearChildren();

        if (!hero.isAlive()) {
            box.setVisible(false);
            return;
        }
        box.setVisible(true);

        box.pad(8f);
        box.top().left();

        box.add(new Label(hero.getName(), skin)).left().row();

        ProgressBar hpBar = new ProgressBar(0, hero.getMaxHp(), 1, false, progressBarStyle);
        hpBar.setValue(hero.getCurrentHp());
        Label hpLabel = new Label(hero.getCurrentHp() + "/" + hero.getMaxHp(), skin);
        hpLabel.setAlignment(Align.center);

        Stack hpStack = new Stack();
        hpStack.add(hpBar);
        hpStack.add(hpLabel);
        box.add(hpStack).width(box.getWidth() - 16).height(20).padTop(5).left().row();

        Label energyLabel = new Label("Energy: " + hero.getEnergy() + "/" + hero.getMaxEnergy(), skin);
        box.add(energyLabel).left().padTop(5).row();

        List<StatusEffect> effects = hero.getActiveEffects();
        if (!effects.isEmpty()) {
            Table effectsTable = new Table();
            effectsTable.left();
            for (StatusEffect effect : effects) {
                Texture iconTexture = statusEffectIcons.get(effect.getName());
                if (iconTexture != null) {
                    Image iconImage = new Image(iconTexture);
                    effectsTable.add(iconImage).size(16, 16).padRight(3);
                }

                String labelText;
                if (effect instanceof VulnerableEffect) {
                    labelText = effect.getName();
                } else {
                    labelText = effect.getName() + " (" + effect.getDuration() + ")";
                }

                Label effectLabel = new Label(labelText, skin);
                effectLabel.setFontScale(0.8f);
                effectLabel.setColor(Color.ORANGE);
                effectsTable.add(effectLabel).padRight(8);
            }
            box.add(effectsTable).left().padTop(5).row();
        }
    }

    private void addHeroClickListener(HeroActor actor) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentState != BattleState.AWAITING_INPUT) return;
                Hero clickedHero = actor.getHero();
                if (currentPlayer.getHeroRoster().contains(clickedHero) && clickedHero.isAlive()) {

                    // Cek apakah hero yang diklik sedang dalam kondisi stun
                    if (clickedHero.isStunned()) {
                        logLabel.setText(clickedHero.getName() + " is stunned and cannot act!");
                        return; // Hentikan proses, hero tidak bisa dipilih
                    }

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
        actionWasTaken = true;

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
        turnCount = 1;
        currentPlayer = player1;
        opponent = player2;
        startNewTurn();
    }

    private void startNewTurn() {
        currentState = BattleState.PROCESSING;
        onTargetSelected = null;
        actionWasTaken = false;

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
        if (currentState == BattleState.PROCESSING && !actionWasTaken) return;
        currentState = BattleState.PROCESSING;

        if (checkForDefeatedHero()) return;

        for (Hero hero : currentPlayer.getHeroRoster()) {
            if (hero.isAlive()) {
                hero.applyAndDecrementEffects();
            }
        }

        if (checkForDefeatedHero()) return;

        Player temp = currentPlayer;
        currentPlayer = opponent;
        opponent = temp;

        if (currentPlayer == player1) {
            turnCount++;
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                startNewTurn();
            }
        }, 0.5f);
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
        turnCounterLabel.setText("Turn: " + turnCount);

        for(int i = 0; i < 3; i++) {
            Hero p1Hero = player1.getHeroRoster().get(i);
            populateStatusBox(p1StatusTables[i], p1Hero);
            p1StatusTables[i].setColor(player1.getActiveHero() == p1Hero ? Color.GOLD : Color.WHITE);

            Hero p2Hero = player2.getHeroRoster().get(i);
            populateStatusBox(p2StatusTables[i], p2Hero);
            p2StatusTables[i].setColor(player2.getActiveHero() == p2Hero ? Color.GOLD : Color.WHITE);
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
    @Override public void dispose() {
        stage.dispose();
        background.dispose();
        skin.dispose();
        for (Texture texture : statusEffectIcons.values()) {
            texture.dispose();
        }
    }
}
