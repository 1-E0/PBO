// PASTE KODE INI UNTUK MENGGANTIKAN SELURUH ISI FILE BattleScreen.java
package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ezra.supersmash.Effects.MockEffect;
import com.ezra.supersmash.Effects.VulnerableEffect;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Rendering.HeroActor;
import com.ezra.supersmash.Rendering.ScrollActor;
import com.ezra.supersmash.Scrolls.ScrollOfHealing;
import com.ezra.supersmash.Scrolls.ScrollOfStunning;
// Pastikan Anda sudah membuat setidaknya satu kelas Scroll, contoh:
// import com.ezra.supersmash.Scrolls.ScrollOfHealing;

import java.util.*;
import java.util.List;
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

    // --- VARIABEL UNTUK SISTEM SCROLL ---
    private Table p1ScrollContainer, p2ScrollContainer;
    private List<Scroll> allPossibleScrolls;
    private boolean scrollWasUsedThisTurn = false;
    // ------------------------------------

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
        initializeScrolls(); // Inisialisasi daftar scroll
        setupUI();
        startNewGame();
    }

    // Metode untuk menampilkan pesan di log terpusat
    public void log(String message) {
        logLabel.setText(message);
    }

    private void initializeScrolls() {
        allPossibleScrolls = new ArrayList<>();

        allPossibleScrolls.add(new ScrollOfHealing());
        allPossibleScrolls.add(new ScrollOfStunning());
    }

    private void loadStatusEffectIcons() {
        try {
            statusEffectIcons.put("Burn", new Texture("icons/burning.png"));
            statusEffectIcons.put("Bleed", new Texture("icons/bleeding.png"));
            statusEffectIcons.put("Stunned", new Texture("icons/Stun.png"));
            statusEffectIcons.put("Vulnerable", new Texture("icons/Vulnerable.png"));
            statusEffectIcons.put("Defense Up", new Texture("icons/defenseup.png"));
            statusEffectIcons.put("Attack Down", new Texture("icons/attackdown.png"));
            statusEffectIcons.put("Mock", new Texture("icons/Mock.png"));
        } catch (Exception e) {
            System.err.println("Gagal memuat ikon status effect: " + e.getMessage());
        }
    }

    private void setupUI() {
        stage.clear();
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // ... (Kode setup hero tetap sama) ...
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
        float statusBoxWidth = 190f;
        float statusBoxHeight = 110f;
        float horizontalOffsetP1Status = 160f;
        float horizontalOffsetP2Status = 180f;

        for (int i = 0; i < 3; i++) {
            float charHeightP1 = baseCharHeight * scales[i];
            Hero p1Hero = player1.getHeroRoster().get(i);
            p1HeroActors[i] = new HeroActor(p1Hero, false);
            float p1CharWidth = charHeightP1 * getAspectRatio(p1Hero);
            p1HeroActors[i].setSize(p1CharWidth, charHeightP1);
            p1HeroActors[i].setPosition(xPositionsP1[i] - (p1CharWidth / 2), yPositions[i]);
            p1StatusTables[i] = new Table();
            p1StatusTables[i].setBackground(statusBoxBg);
            p1StatusTables[i].setSize(statusBoxWidth, statusBoxHeight);
            float p1BoxX = p1HeroActors[i].getX() + (p1CharWidth / 2) - (p1StatusTables[i].getWidth() / 2) - horizontalOffsetP1Status;
            float p1BoxY = p1HeroActors[i].getY() - 20f;
            if (i > 0) {
                Table previousBox = p1StatusTables[i - 1];
                float bottomOfPreviousBox = previousBox.getY() - previousBox.getHeight();
                if (p1BoxY > bottomOfPreviousBox) p1BoxY = bottomOfPreviousBox - 5f;
            }
            p1StatusTables[i].setPosition(p1BoxX, p1BoxY);
            stage.addActor(p1StatusTables[i]);
            stage.addActor(p1HeroActors[i]);
            addHeroClickListener(p1HeroActors[i]);
            float charHeightP2 = baseCharHeight * scales[i];
            Hero p2Hero = player2.getHeroRoster().get(i);
            p2HeroActors[i] = new HeroActor(p2Hero, true);
            float p2CharWidth = charHeightP2 * getAspectRatio(p2Hero);
            p2HeroActors[i].setSize(p2CharWidth, charHeightP2);
            p2HeroActors[i].setPosition(xPositionsP2[i] - (p2CharWidth / 2), yPositions[i]);
            p2StatusTables[i] = new Table();
            p2StatusTables[i].setBackground(statusBoxBg);
            p2StatusTables[i].setSize(statusBoxWidth, statusBoxHeight);
            float p2BoxX = p2HeroActors[i].getX() + (p2CharWidth / 2) - (p2StatusTables[i].getWidth() / 2) + horizontalOffsetP2Status;
            float p2BoxY = p2HeroActors[i].getY() - 20f;
            if (i > 0) {
                Table previousBox = p2StatusTables[i - 1];
                float bottomOfPreviousBox = previousBox.getY() - previousBox.getHeight();
                if (p2BoxY > bottomOfPreviousBox) p2BoxY = bottomOfPreviousBox - 5f;
            }
            p2StatusTables[i].setPosition(p2BoxX, p2BoxY);
            stage.addActor(p2StatusTables[i]);
            stage.addActor(p2HeroActors[i]);
            addHeroClickListener(p2HeroActors[i]);
        }

        // --- SETUP UI UNTUK SCROLL ---
        p1ScrollContainer = new Table();
// Posisikan tabel di tepi kiri dan buat tingginya sama dengan tinggi layar
        p1ScrollContainer.setPosition(50f, 0);
        p1ScrollContainer.setHeight(Gdx.graphics.getHeight());
        p1ScrollContainer.center(); // Pusatkan semua konten di dalam tabel secara vertikal
        stage.addActor(p1ScrollContainer);

        p2ScrollContainer = new Table();
// Posisikan tabel di tepi kanan (lebar layar - lebar scroll - padding)
        p2ScrollContainer.setPosition(Gdx.graphics.getWidth() - 50f , 0);
        p2ScrollContainer.setHeight(Gdx.graphics.getHeight());
        p2ScrollContainer.center(); // Pusatkan semua konten di dalam tabel secara vertikal
        stage.addActor(p2ScrollContainer);
        // -----------------------------

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
        float availableWidth = box.getWidth() - (box.getPadLeft() + box.getPadRight());
        box.add(hpStack).width(availableWidth).height(20).padTop(5).left().row();
        Label energyLabel = new Label("Energy: " + hero.getEnergy() + "/" + hero.getMaxEnergy(), skin);
        box.add(energyLabel).left().padTop(5).row();
        List<StatusEffect> effects = hero.getActiveEffects();
        if (!effects.isEmpty()) {
            HorizontalGroup effectsGroup = new HorizontalGroup();
            effectsGroup.wrap();
            effectsGroup.space(4f);
            effectsGroup.wrapSpace(2f);
            effectsGroup.rowAlign(Align.left);
            for (StatusEffect effect : effects) {
                Table singleEffect = new Table();
                singleEffect.add().width(3f);
                Texture iconTexture = statusEffectIcons.get(effect.getName());
                if (iconTexture != null) {
                    Image iconImage = new Image(iconTexture);
                    singleEffect.add(iconImage).size(16, 16).padRight(3f).align(Align.bottom);
                }
                String labelText;
                if (effect instanceof VulnerableEffect) labelText = effect.getName();
                else labelText = effect.getName() + " (" + effect.getDuration() + ")";
                Label effectLabel = new Label(labelText, skin);
                effectLabel.setFontScale(0.8f);
                effectLabel.setColor(Color.ORANGE);
                BitmapFont font = effectLabel.getStyle().font;
                float fontLineHeight = font.getLineHeight() * effectLabel.getFontScaleY();
                singleEffect.add(effectLabel).height(fontLineHeight);
                effectsGroup.addActor(singleEffect);
            }
            box.add(effectsGroup).width(availableWidth).left().padTop(5f).row();
        }
    }

    private void addHeroClickListener(HeroActor actor) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentState != BattleState.AWAITING_INPUT) return;
                Hero clickedHero = actor.getHero();
                if (currentPlayer.getHeroRoster().contains(clickedHero) && clickedHero.isAlive()) {
                    if (clickedHero.isStunned()) {
                        log(clickedHero.getName() + " is stunned and cannot act!");
                        return;
                    }
                    currentPlayer.setActiveHero(currentPlayer.getHeroRoster().indexOf(clickedHero));
                    log(clickedHero.getName() + " is active! Select an action or target.");
                } else if (currentPlayer.getActiveHero() != null && opponent.getHeroRoster().contains(clickedHero) && clickedHero.isAlive()) {
                    Hero mockingHero = null;
                    for (Hero hero : opponent.getHeroRoster()) {
                        if (hero.isAlive()) {
                            for (StatusEffect effect : hero.getActiveEffects()) {
                                if (effect instanceof MockEffect) {
                                    mockingHero = hero;
                                    break;
                                }
                            }
                        }
                        if (mockingHero != null) break;
                    }
                    if (mockingHero != null && clickedHero != mockingHero) {
                        log("You must attack " + mockingHero.getName() + " due to Mock!");
                        return;
                    }
                    if (onTargetSelected != null) {
                        onTargetSelected.accept(clickedHero);
                    }
                }
            }
        });
    }

    // --- METODE-METODE BARU UNTUK SCROLL ---
    private void awardScrolls() {
        if (allPossibleScrolls.isEmpty()) {
            System.err.println("WARNING: Daftar 'allPossibleScrolls' kosong. Tidak ada scroll yang bisa diberikan.");
            return;
        }

        Random rand = new Random();
        log("A new scroll has been granted to each player!");

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (player1.canAddScroll()) {
                    Scroll newScroll = allPossibleScrolls.get(rand.nextInt(allPossibleScrolls.size()));
                    player1.addScroll(createScrollInstance(newScroll.getClass()));
                }
                if (player2.canAddScroll()) {
                    Scroll newScroll = allPossibleScrolls.get(rand.nextInt(allPossibleScrolls.size()));
                    player2.addScroll(createScrollInstance(newScroll.getClass()));
                }
            }
        }, 0.5f); // Jeda sedikit untuk notifikasi
    }

    private Scroll createScrollInstance(Class<? extends Scroll> scrollClass) {
        try {
            return scrollClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void useScroll(Scroll scroll) {
        if (currentState != BattleState.AWAITING_INPUT || actionWasTaken || scrollWasUsedThisTurn) {
            log("You cannot use a scroll right now.");
            return;
        }

        currentState = BattleState.PROCESSING;
        scrollWasUsedThisTurn = true;
        actionWasTaken = true;

        scroll.activate(currentPlayer, opponent, this);
        currentPlayer.removeScroll(scroll);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                endTurn();
            }
        }, 1.5f);
    }

    private void updateScrollUI() {
        if (p1ScrollContainer == null || p2ScrollContainer == null) return;
        p1ScrollContainer.clear();
        p2ScrollContainer.clear();

        for (Scroll scroll : player1.getScrolls()) {
            boolean canUse = (currentPlayer == player1 && !actionWasTaken);
            // Tambahkan 'skin' sebagai argumen ketiga
            ScrollActor actor = new ScrollActor(scroll, this, skin, canUse);
            p1ScrollContainer.add(actor).padBottom(10);
            p1ScrollContainer.row();
        }

        for (Scroll scroll : player2.getScrolls()) {
            boolean canUse = (currentPlayer == player2 && !actionWasTaken);
            // Tambahkan 'skin' sebagai argumen ketiga
            ScrollActor actor = new ScrollActor(scroll, this, skin, canUse);
            p2ScrollContainer.add(actor).padBottom(10);
            p2ScrollContainer.row();
        }
    }
    // ----------------------------------------

    private void addActionListeners() {
        attackButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (attackButton.isDisabled()) return;
                Hero activeHero = currentPlayer.getActiveHero();
                if (activeHero.getEnergy() < 1) { log("Not enough energy!"); return; }
                log("ATTACK: Select an enemy target.");
                onTargetSelected = (target) -> {
                    activeHero.spendEnergy(1);
                    executeAction(activeHero, target, () -> {
                        activeHero.basicAttack(target);
                        log(activeHero.getName() + " attacks " + target.getName() + "!");
                    });
                };
            }
        });

        skillButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (skillButton.isDisabled()) return;
                Hero activeHero = currentPlayer.getActiveHero();
                if (activeHero.getEnergy() < 3) { log("Not enough energy!"); return; }
                log("SKILL: Select an enemy target.");
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
        processFloatingTextQueue();

        // === BAGIAN TES DIAGNOSTIK ===
        stage.getBatch().begin();

        // SECARA PAKSA ATUR WARNA MENJADI PUTIH TEPAT SEBELUM MENGGAMBAR BACKGROUND
        stage.getBatch().setColor(Color.WHITE);

        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        // === AKHIR BAGIAN TES ===

        stage.act(delta);
        stage.draw();
    }

    private void startNewGame() {
        turnCount = 1;
        currentPlayer = player1;
        opponent = player2;

        // ================================================================
        // ===== JALAN PINTAS UNTUK DEBUGGING (Berikan Scroll Instan) =====
        // ================================================================
        // Pastikan daftar scroll tidak kosong sebelum mencoba memberikannya.
        if (allPossibleScrolls != null && !allPossibleScrolls.isEmpty()) {
            // Beri 2 scroll ke Player 1 agar bisa langsung tes tumpukan vertikal.
            player1.addScroll(createScrollInstance(allPossibleScrolls.get(0).getClass()));

            // Cek jika ada lebih dari satu jenis scroll, untuk menghindari error.
            if (allPossibleScrolls.size() > 1) {
                player1.addScroll(createScrollInstance(allPossibleScrolls.get(1).getClass()));
            } else {
                player1.addScroll(createScrollInstance(allPossibleScrolls.get(0).getClass()));
            }

            // Beri 1 scroll ke Player 2 untuk pengetesan.
            player2.addScroll(createScrollInstance(allPossibleScrolls.get(0).getClass()));
        }
        // ================================================================
        // ================= AKHIR JALAN PINTAS DEBUG =====================
        // ================================================================

        startNewTurn();
    }
    private void startNewTurn() {
        currentState = BattleState.PROCESSING;
        onTargetSelected = null;
        actionWasTaken = false;
        scrollWasUsedThisTurn = false;

        for (Hero hero : currentPlayer.getHeroRoster()) {
            if (hero.isAlive()) hero.gainEnergy(1);
        }

        currentPlayer.setActiveHero(-1);
        log(currentPlayer.getName() + "'s turn. Select your character.");
        currentState = BattleState.AWAITING_INPUT;
    }

    private void endTurn() {
        if (currentState == BattleState.PROCESSING && !actionWasTaken) return;
        currentState = BattleState.PROCESSING;

        if (checkForDefeatedHero()) return;

        for (Hero hero : currentPlayer.getHeroRoster()) {
            if (hero.isAlive()) hero.applyAndDecrementEffects();
        }

        if (checkForDefeatedHero()) return;

        Player temp = currentPlayer;
        currentPlayer = opponent;
        opponent = temp;

        if (currentPlayer == player1) {
            turnCount++;
            // Beri scroll setiap 2 giliran, dimulai dari giliran ke-2
            if (turnCount > 1 && turnCount % 2 == 0) {
                awardScrolls();
            }
        }

        Timer.schedule(new Timer.Task() {
            @Override public void run() {
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

        updateScrollUI();

        boolean canAct = currentState == BattleState.AWAITING_INPUT && currentPlayer.getActiveHero() != null && !scrollWasUsedThisTurn;
        attackButton.setDisabled(!canAct);
        skillButton.setDisabled(!canAct);
        endTurnButton.setDisabled(currentState != BattleState.AWAITING_INPUT);
    }

    private void processFloatingTextQueue() {
        if (!Hero.damageQueue.isEmpty()) {
            for (Hero.DamageInfo info : Hero.damageQueue) {
                HeroActor targetActor = null;
                for (HeroActor actor : p1HeroActors) {
                    if (actor.getHero() == info.target) {
                        targetActor = actor;
                        break;
                    }
                }
                if (targetActor == null) {
                    for (HeroActor actor : p2HeroActors) {
                        if (actor.getHero() == info.target) {
                            targetActor = actor;
                            break;
                        }
                    }
                }
                if (targetActor != null) {
                    String text = String.valueOf(info.amount);
                    Color color = Color.WHITE;
                    if (info.isCritical) {
                        text = "CRIT! " + text;
                        color = Color.YELLOW;
                    }
                    FloatingText ft = new FloatingText(text, skin, color);
                    ft.setPosition(targetActor.getX() + targetActor.getWidth() / 2 - ft.getPrefWidth() / 2, targetActor.getY() + targetActor.getHeight());
                    stage.addActor(ft);
                    ft.animate();
                }
            }
            Hero.damageQueue.clear();
        }
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
        // Dispose sisa scroll
        if (allPossibleScrolls != null) {
            for(Scroll s : allPossibleScrolls) s.dispose();
        }
        if (player1 != null) {
            for(Scroll s : player1.getScrolls()) s.dispose();
        }
        if (player2 != null) {
            for(Scroll s : player2.getScrolls()) s.dispose();
        }
    }
}
