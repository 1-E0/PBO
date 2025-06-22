package com.ezra.supersmash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ezra.supersmash.Effects.MockEffect;
import com.ezra.supersmash.Effects.VulnerableEffect;
import com.ezra.supersmash.Heroes.*;
import com.ezra.supersmash.Rendering.AnimationComponent;
import com.ezra.supersmash.Rendering.HeroActor;
import com.ezra.supersmash.Rendering.ScrollActor;
import com.ezra.supersmash.Rendering.VisualEffectActor;
import com.ezra.supersmash.Scrolls.*;


import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class BattleScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private Player player1, player2, currentPlayer, opponent;
    private Sound crit;
    private Music battleMusic; // Variabel untuk musik pertempuran

    // UI Battle
    private Label turnLabel, logLabel, turnCounterLabel;
    private int turnCount;
    private Table[] p1StatusTables = new Table[3];
    private Table[] p2StatusTables = new Table[3];
    private HeroActor[] p1HeroActors = new HeroActor[3];
    private HeroActor[] p2HeroActors = new HeroActor[3];
    private TextButton attackButton, skillButton, useScrollButton, endTurnButton;
    private ProgressBar.ProgressBarStyle progressBarStyle;
    private Map<String, Texture> statusEffectIcons;
    private Table p1ScrollContainer, p2ScrollContainer;
    private Table manualTooltip;
    private Label manualTooltipLabel;
    private boolean scrollWasUsedThisTurn = false;
    private boolean actionWasTaken = false;

    // UI & Logic Draft
    private Table draftScrollContainer;
    private Player draftPlayer;
    private Player draftStarter;
    private List<Scroll> draftableScrolls;
    private List<Scroll> allPossibleScrolls;


    private enum BattleState { SCROLL_DRAFT, AWAITING_INPUT, PROCESSING, SELECTING_SCROLL }
    private BattleState currentState;
    public Consumer<Hero> onTargetSelected;


    public BattleScreen(Main game, Player player1, Player player2) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        background = new Texture(Gdx.files.internal(new String[]{"backgrounds/game_background_1.png", "backgrounds/game_background_2.png", "backgrounds/game_background_3.png", "backgrounds/game_background_4.png"}[new Random().nextInt(4)]));
        crit = Gdx.audio.newSound(Gdx.files.internal("sounds/crit.mp3"));

        // Memuat dan mengatur musik pertempuran
        battleMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/battlesong.mp3"));
        battleMusic.setLooping(true);
        battleMusic.setVolume(0.08f);

        progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        statusEffectIcons = new HashMap<>();

        loadStatusEffectIcons();
        initializeScrolls();
        startScrollDraft(); // Mulai dari fase draft
    }

    public void playEffectAnimation(Hero targetHero, String animationPath, int frameCols, int frameRows, int rowIndex, float frameDuration) {
        HeroActor targetActor = null;
        // Cari Actor yang sesuai dengan Hero target
        for (HeroActor actor : p1HeroActors) {
            if (actor.getHero() == targetHero) {
                targetActor = actor;
                break;
            }
        }
        if (targetActor == null) {
            for (HeroActor actor : p2HeroActors) {
                if (actor.getHero() == targetHero) {
                    targetActor = actor;
                    break;
                }
            }
        }

        if (targetActor != null) {
            // Buat animasi dari file yang diberikan
            Animation<TextureRegion> effectAnimation = VisualEffectActor.createEffectAnimation(animationPath, frameCols, frameRows, rowIndex, frameDuration);
            VisualEffectActor effectActor = new VisualEffectActor(effectAnimation);

            // Atur ukuran dan posisi efek agar berada di tengah Hero
            float effectScale = 2.0f; // Anda bisa sesuaikan skala efek di sini
            effectActor.setSize(effectActor.getWidth() * effectScale, effectActor.getHeight() * effectScale);
            float effectX = targetActor.getX() + (targetActor.getWidth() / 2) - (effectActor.getWidth() / 2);
            float effectY = targetActor.getY() + (targetActor.getHeight() / 2) - (effectActor.getHeight() / 2);

            // =================================================================
            // ---            BLOK KUSTOMISASI POSISI EFEK                 ---
            // Anda bisa menambahkan logika custom untuk setiap hero di sini.
            // =================================================================
            if (targetHero instanceof Warrior) {
                if (player1.getHeroRoster().contains(targetHero)) { // Jika Warrior milik Player 1
                    effectY -= 40; // Contoh: geser efek sedikit ke atas
                    effectX -= 40; // Contoh: geser efek sedikit ke kiri
                } else { // Jika Warrior milik Player 2
                    effectY -= 40;
                    effectX += 40;
                }
            }

            if (targetHero instanceof Mage) {
                if (player1.getHeroRoster().contains(targetHero)) { // Jika Warrior milik Player 1
                    effectY -= 30; // Contoh: geser efek sedikit ke atas
                    effectX -= 20; // Contoh: geser efek sedikit ke kiri
                } else { // Jika Warrior milik Player 2
                    effectY -= 30;
                    effectX += 20;
                }
            }

            if (targetHero instanceof Archer) {
                if (player1.getHeroRoster().contains(targetHero)) { // Jika Warrior milik Player 1
                    effectY -= 30; // Contoh: geser efek sedikit ke atas
                    effectX -= 0; // Contoh: geser efek sedikit ke kiri
                } else { // Jika Warrior milik Player 2
                    effectY -= 30;
                    effectX += 0;
                }
            }

            if (targetHero instanceof Assassin) {
                if (player1.getHeroRoster().contains(targetHero)) { // Jika Warrior milik Player 1
                    effectY -= 40; // Contoh: geser efek sedikit ke atas
                    effectX -= 5; // Contoh: geser efek sedikit ke kiri
                } else { // Jika Warrior milik Player 2
                    effectY -= 40;
                    effectX += 5;
                }
            }

            if (targetHero instanceof Tank) {
                if (player1.getHeroRoster().contains(targetHero)) { // Jika Warrior milik Player 1
                    effectY -= 30; // Contoh: geser efek sedikit ke atas
                    effectX -= 5; // Contoh: geser efek sedikit ke kiri
                } else { // Jika Warrior milik Player 2
                    effectY -= 30;
                    effectX += 5;
                }
            }
            // Tambahkan hero lain di sini dengan 'else if'
            /*
            else if (targetHero instanceof Mage) {
                effectY += 50; // Contoh: Efek untuk Mage muncul lebih tinggi
            }
            */
            // =================================================================
            // ---               AKHIR BLOK KUSTOMISASI                    ---
            // =================================================================

            // 2. Terapkan posisi final
            effectActor.setPosition(effectX, effectY);

            // 3. Tambahkan efek ke stage
            stage.addActor(effectActor);
        }
    }

    private void startScrollDraft() {
        currentState = BattleState.SCROLL_DRAFT;
        draftableScrolls = new ArrayList<>();
        Collections.shuffle(allPossibleScrolls);
        for (int i = 0; i < 6; i++) {
            draftableScrolls.add(createScrollInstance(allPossibleScrolls.get(i).getClass()));
        }

        // Tentukan siapa yang mulai draft
        draftStarter = new Random().nextBoolean() ? player1 : player2;
        draftPlayer = draftStarter;

        setupDraftUI();
    }

    private void setupDraftUI() {
        stage.clear();

        Stack uiStack = new Stack();
        uiStack.setFillParent(true);
        stage.addActor(uiStack);

        Image bgImage = new Image(background);
        uiStack.add(bgImage);

        Table root = new Table();
        root.setFillParent(true);
        uiStack.add(root);

        turnLabel = new Label("", skin, "highlighted");
        turnLabel.setFontScale(1.5f);
        root.add(turnLabel).pad(20).top().row();

        draftScrollContainer = new Table();
        for (Scroll scroll : draftableScrolls) {
            ScrollActor actor = new ScrollActor(scroll, this, true);
            draftScrollContainer.add(actor).pad(15);
        }
        root.add(draftScrollContainer).expand().row();

        logLabel = new Label("A new battle begins! Choose your scrolls.", skin, "default");
        logLabel.setFontScale(1.2f);
        root.add(logLabel).pad(20).bottom();

        // Tooltip
        manualTooltip = new Table(skin);
        manualTooltip.setBackground(skin.newDrawable("list", new Color(0.1f, 0.1f, 0.1f, 0.9f)));
        manualTooltipLabel = new Label("", skin, "default");
        manualTooltipLabel.setWrap(true);
        manualTooltip.add(manualTooltipLabel).width(180).pad(8f);
        manualTooltip.setVisible(false);
        stage.addActor(manualTooltip);

        updateDraftUI();
    }

    public void handleScrollDraftPick(Scroll scroll) {
        if (currentState != BattleState.SCROLL_DRAFT || !draftableScrolls.contains(scroll)) {
            return;
        }

        draftPlayer.addScroll(scroll);
        draftableScrolls.remove(scroll);

        // Animasikan scroll yang dipilih
        for (Actor actor : draftScrollContainer.getChildren()) {
            ScrollActor scrollActor = (ScrollActor) actor;
            if (scrollActor.getScroll() == scroll) {
                scrollActor.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
                scrollActor.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.scaleTo(1.5f, 1.5f, 0.2f),
                        Actions.fadeOut(0.2f)
                    ),
                    Actions.removeActor()
                ));
                break;
            }
        }

        // Cek jika draft selesai
        if (player1.getScrolls().size() + player2.getScrolls().size() == 6) {
            logLabel.setText("Draft complete! Preparing for battle...");
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    endScrollDraft();
                }
            }, 1.5f);
        } else {
            // Ganti giliran draft
            draftPlayer = (draftPlayer == player1) ? player2 : player1;
            updateDraftUI();
        }
    }


    private void endScrollDraft() {
        // Player yang pick KEDUA akan jalan PERTAMA
        currentPlayer = (draftStarter == player1) ? player2 : player1;
        opponent = (currentPlayer == player1) ? player2 : player1;

        setupUI(); // Setup UI untuk battle
        startNewGame(); // Mulai game
    }

    private void updateDraftUI() {
        if (currentState != BattleState.SCROLL_DRAFT) return;
        turnLabel.setText(draftPlayer.getName() + ": Pick a Scroll (" + (draftPlayer.getScrolls().size() + 1) + "/3)");
    }


    public void showManualTooltip(Scroll scroll, float screenX, float screenY) {
        manualTooltipLabel.setText(scroll.getDescription());
        manualTooltip.pack();

        float tooltipX = screenX + 15f;
        float tooltipY = screenY - 10f - manualTooltip.getHeight();

        if (tooltipX + manualTooltip.getWidth() > stage.getWidth()) {
            tooltipX = screenX - 15f - manualTooltip.getWidth();
        }
        if (tooltipY < 0) {
            tooltipY = screenY + 15f;
        }

        manualTooltip.setPosition(tooltipX, tooltipY);
        manualTooltip.setVisible(true);
    }

    public void hideManualTooltip() {
        manualTooltip.setVisible(false);
    }

    public boolean isScrollUsable(Scroll scroll) {
        if (currentPlayer.getScrolls().contains(scroll)) {
            // Perbaikan: Izinkan penggunaan scroll jika tidak ada aksi yang diambil (kecuali scroll itu sendiri)
            return currentState == BattleState.SELECTING_SCROLL && !scrollWasUsedThisTurn;
        }
        return false;
    }



    public void useScroll(Scroll scroll, Hero target) {
        if (!isScrollUsable(scroll)) {
            return;
        }

        currentState = BattleState.PROCESSING;
        boolean success = scroll.activate(currentPlayer, opponent, target, this);

        if (success) {
            // Aksi berhasil, scroll dikonsumsi dan giliran berakhir
            scrollWasUsedThisTurn = true;
            actionWasTaken = true;
            currentPlayer.removeScroll(scroll);

            // Setelah menggunakan scroll, giliran otomatis berakhir
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    endTurn();
                }
            }, 1.5f);
        } else {
            // Jika gagal (misal, target salah), kembalikan state agar pemain bisa memilih aksi lain
            // Pesan error "fizzles out" sudah di-log dari dalam metode activate scroll
            currentState = BattleState.AWAITING_INPUT;
            onTargetSelected = null; // Batalkan pemilihan target
            // Jangan set actionWasTaken atau scrollWasUsedThisTurn ke true
        }
    }


    public void log(String message) {
        if(logLabel != null) logLabel.setText(message);
    }

    private void initializeScrolls() {
        allPossibleScrolls = new ArrayList<>();
        allPossibleScrolls.add(new ScrollOfHealing());
        allPossibleScrolls.add(new ScrollOfStunning());
        allPossibleScrolls.add(new ScrollOfFireball());
        allPossibleScrolls.add(new ScrollOfPower());
        allPossibleScrolls.add(new ScrollOfShielding());
        allPossibleScrolls.add(new ScrollOfVulnerability()); // Asumsi ada ScrollOfVulnerability
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
            statusEffectIcons.put("Attack Up", new Texture("icons/defenseup.png")); // Placeholder icon
        } catch (Exception e) {
            System.err.println("Failed to load status effect icons: " + e.getMessage());
        }
    }

    private void setupUI() {
        stage.clear();

        Stack uiStack = new Stack();
        uiStack.setFillParent(true);
        stage.addActor(uiStack);

        Image bgImage = new Image(background);
        uiStack.add(bgImage);

        Table root = new Table();
        root.setFillParent(true);
        uiStack.add(root);

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

        p1ScrollContainer = new Table();
        p1ScrollContainer.setPosition(50f, 0);
        p1ScrollContainer.setHeight(Gdx.graphics.getHeight());
        p1ScrollContainer.center();
        stage.addActor(p1ScrollContainer);

        p2ScrollContainer = new Table();
        p2ScrollContainer.setPosition(Gdx.graphics.getWidth() - 50f , 0);
        p2ScrollContainer.setHeight(Gdx.graphics.getHeight());
        p2ScrollContainer.center();
        stage.addActor(p2ScrollContainer);

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
        logLabel.setFontScale(1.4f); // <-- TAMBAHKAN BARIS INI untuk memperbesar teks
        logLabel.setColor(Color.WHITE); // <-- TAMBAHKAN BARIS INI untuk mengubah warna jadi putih
        topCenterContainer.add(turnLabel).pad(10).row();
        topCenterContainer.add(logLabel).width(screenWidth * 0.4f).row();
        topUiStack.add(topCenterContainer);
        topUiStack.add(topLeftContainer);
        root.add(topUiStack).expandX().fillX().top().row();

        Table actionTable = new Table();
        attackButton = new TextButton("Attack (1)", skin);
        skillButton = new TextButton("Skill (3)", skin);
        useScrollButton = new TextButton("Use Scroll", skin);
        endTurnButton = new TextButton("End Turn", skin);
        float buttonWidth = 150f;
        float buttonHeight = 50f;
        float buttonSpacing = 15f;
        actionTable.add(attackButton).width(buttonWidth).height(buttonHeight);
        actionTable.add(skillButton).width(buttonWidth).height(buttonHeight).padLeft(buttonSpacing);
        actionTable.add(useScrollButton).width(buttonWidth).height(buttonHeight).padLeft(buttonSpacing);
        actionTable.add(endTurnButton).width(buttonWidth).height(buttonHeight).padLeft(buttonSpacing);
        float buttonAlpha = 0.9f;
        attackButton.getColor().a = buttonAlpha;
        skillButton.getColor().a = buttonAlpha;
        useScrollButton.getColor().a = buttonAlpha;
        endTurnButton.getColor().a = buttonAlpha;

        Table bottomBar = new Table();
        bottomBar.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.7f)));
        bottomBar.add(actionTable).pad(10f);
        root.add(new Table()).expandY().row();
        root.add(bottomBar).fillX().height(buttonHeight + 20f);

        addActionListeners();

        manualTooltip = new Table(skin);
        manualTooltip.setBackground(skin.newDrawable("list", new Color(0.1f, 0.1f, 0.1f, 0.9f)));
        manualTooltipLabel = new Label("", skin, "default");
        manualTooltipLabel.setWrap(true);
        manualTooltip.add(manualTooltipLabel).width(180).pad(8f);
        manualTooltip.setVisible(false);
        stage.addActor(manualTooltip);
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
                // Modifikasi kondisi untuk pemilihan target scroll
                if (currentState == BattleState.SELECTING_SCROLL) {
                    Hero clickedHero = actor.getHero();
                    // Jika target adalah musuh dan valid, panggil onTargetSelected
                    if (opponent.getHeroRoster().contains(clickedHero) && clickedHero.isAlive()) {
                        if (onTargetSelected != null) {
                            onTargetSelected.accept(clickedHero);
                        }
                    } else if (currentPlayer.getHeroRoster().contains(clickedHero) && clickedHero.isAlive()) { // Untuk scroll yang menargetkan sekutu
                        if (onTargetSelected != null) {
                            onTargetSelected.accept(clickedHero);
                        }
                    }
                    return; // Penting untuk menghentikan lebih lanjut jika dalam mode pemilihan scroll
                }


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


    private void awardScrolls() {
        if (allPossibleScrolls.isEmpty()) {
            System.err.println("WARNING: 'allPossibleScrolls' list is empty. No scrolls can be awarded.");
            return;
        }
        Random rand = new Random();
        log("A new scroll has been granted to each player!");
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (player1.canAddScroll()) {
                    player1.addScroll(createScrollInstance(allPossibleScrolls.get(rand.nextInt(allPossibleScrolls.size())).getClass()));
                }
                if (player2.canAddScroll()) {
                    player2.addScroll(createScrollInstance(allPossibleScrolls.get(rand.nextInt(allPossibleScrolls.size())).getClass()));
                }
            }
        }, 0.5f);
    }

    private Scroll createScrollInstance(Class<? extends Scroll> scrollClass) {
        try {
            return scrollClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addActionListeners() {
        attackButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (attackButton.isDisabled()) return;
                Hero activeHero = currentPlayer.getActiveHero();
                if (activeHero == null) { log("Please select a hero first."); return; }
                if (activeHero.getEnergy() < 1) { log("Not enough energy!"); return; }
                if (actionWasTaken) { log("You have already taken an action this turn."); return; }
                log("Attack: Select an enemy target.");
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
                if (activeHero == null) { log("Please select a hero first."); return; }
                if (activeHero.getEnergy() < 3) { log("Not enough energy!"); return; }
                if (actionWasTaken) { log("You have already taken an action this turn."); return; }
                log("Skill: Select an enemy target.");
                onTargetSelected = (target) -> {
                    activeHero.spendEnergy(3);
                    executeAction(activeHero, target, () -> activeHero.useSkill(target));
                };
            }
        });

        useScrollButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (useScrollButton.isDisabled()) return;
                if (scrollWasUsedThisTurn) { log("You can only use one scroll per turn."); return; }
                if (actionWasTaken) { log("You cannot use a scroll after attacking or using a skill."); return; }

                currentState = BattleState.SELECTING_SCROLL;
                log("Select a scroll to use, then select a target.");
                // Logika baru untuk pemilihan target scroll ada di ScrollActor touchUp
                // Tidak perlu menyetel onTargetSelected di sini, akan disetel oleh ScrollActor
            }
        });


        endTurnButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (endTurnButton.isDisabled()) return;
                if (currentState == BattleState.SELECTING_SCROLL) {
                    currentState = BattleState.AWAITING_INPUT;
                    log("Scroll selection cancelled.");
                    onTargetSelected = null; // Batalkan pemilihan target
                    return;
                }
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

        if (currentState != BattleState.SCROLL_DRAFT) {
            updateUI();
            processFloatingTextQueue();
        } else {
            updateDraftUI();
        }

        stage.act(delta);
        stage.draw();
    }

    private void startNewGame() {
        turnCount = 1;
        startNewTurn();
    }

    private void startNewTurn() {
        currentState = BattleState.AWAITING_INPUT;
        onTargetSelected = null;
        actionWasTaken = false;
        scrollWasUsedThisTurn = false;

        for (Hero hero : currentPlayer.getHeroRoster()) {
            if (hero.isAlive()) hero.gainEnergy(1);
        }
        int firstAvailableHeroIndex = -1;
        for (int i = 0; i < currentPlayer.getHeroRoster().size(); i++) {
            Hero hero = currentPlayer.getHeroRoster().get(i);
            if (hero.isAlive() && !hero.isStunned()) {
                firstAvailableHeroIndex = i;
                break;
            }
        }

        if (firstAvailableHeroIndex != -1) {
            currentPlayer.setActiveHero(firstAvailableHeroIndex);
            log(currentPlayer.getName() + "'s Turn. " + currentPlayer.getActiveHero().getName() + " is active.");
        } else {
            // Jika tidak ada hero yang bisa beraksi (semua stun atau kalah)
            currentPlayer.setActiveHero(-1);
            log(currentPlayer.getName() + "'s Turn. No available heroes to act.");
        }
        currentPlayer.setActiveHero(-1);
        log(currentPlayer.getName() + "'s Turn. Select your character.");
    }

    private void endTurn() {
        if (currentState == BattleState.PROCESSING && !actionWasTaken) return;
        currentState = BattleState.PROCESSING;

        if (checkForDefeatedHero()) return;

        // Terapkan efek DOT/HOT dan decrement durasi di akhir giliran player saat ini
        for (Hero hero : currentPlayer.getHeroRoster()) {
            if (hero.isAlive()) hero.applyAndDecrementEffects();
        }
        for (Hero hero : opponent.getHeroRoster()) {
            if (hero.isAlive()) hero.applyAndDecrementEffects();
        }


        if (checkForDefeatedHero()) return;

        Player temp = currentPlayer;
        currentPlayer = opponent;
        opponent = temp;

        if (currentPlayer == player1) {
            turnCount++;
            if (turnCount > 1 && turnCount % 4 != 0) { // Award setiap awal giliran ganjil (setelah P2 selesai)
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

    private void updateScrollUI() {
        if (p1ScrollContainer == null || p2ScrollContainer == null) return;
        p1ScrollContainer.clear();
        p2ScrollContainer.clear();

        float screenWidth = Gdx.graphics.getWidth(); // Dapatkan lebar jendela saat ini
        float screenHeight = Gdx.graphics.getHeight();

        float scrollHeight = screenHeight * 0.18f; // Sesuaikan persentase ini sesuai keinginan
        float scrollWidth = scrollHeight * (80f / 110f);


        for (Scroll scroll : player1.getScrolls()) {
            ScrollActor actor = new ScrollActor(scroll, this, false);
            actor.setSize(160, 220);
            actor.setSize(scrollWidth, scrollHeight);
            p1ScrollContainer.add(actor).padBottom(10);
            p1ScrollContainer.row();
        }

        for (Scroll scroll : player2.getScrolls()) {
            ScrollActor actor = new ScrollActor(scroll, this, false);
            actor.setSize(160, 220);
            actor.setSize(scrollWidth, scrollHeight);
            p2ScrollContainer.add(actor).padBottom(10);
            p2ScrollContainer.row();
        }
    }

    private void updateUI() {
        turnLabel.setText(currentPlayer.getName() + "'s Turn");
        turnCounterLabel.setText("Turn: " + turnCount);

        for(int i = 0; i < 3; i++) {
            Hero p1Hero = player1.getHeroRoster().get(i);
            populateStatusBox(p1StatusTables[i], p1Hero);
            p1StatusTables[i].setColor(player1.getActiveHero() == p1Hero ? Color.GOLD : Color.WHITE);
            // Perbaikan: Highlight hero musuh saat memilih target scroll
            if (currentState == BattleState.SELECTING_SCROLL && opponent.getHeroRoster().contains(p1Hero) && p1Hero.isAlive()) {
                p1StatusTables[i].setColor(Color.RED); // Warna highlight untuk target musuh
            }
            if (currentState == BattleState.SELECTING_SCROLL && currentPlayer.getHeroRoster().contains(p1Hero) && p1Hero.isAlive()) {
                // Jika scroll menargetkan sekutu, highlight sekutu juga
                p1StatusTables[i].setColor(Color.BLUE); // Warna highlight untuk target sekutu
            }


            Hero p2Hero = player2.getHeroRoster().get(i);
            populateStatusBox(p2StatusTables[i], p2Hero);
            p2StatusTables[i].setColor(player2.getActiveHero() == p2Hero ? Color.GOLD : Color.WHITE);
            // Perbaikan: Highlight hero musuh saat memilih target scroll
            if (currentState == BattleState.SELECTING_SCROLL && opponent.getHeroRoster().contains(p2Hero) && p2Hero.isAlive()) {
                p2StatusTables[i].setColor(Color.RED); // Warna highlight untuk target musuh
            }
            if (currentState == BattleState.SELECTING_SCROLL && currentPlayer.getHeroRoster().contains(p2Hero) && p2Hero.isAlive()) {
                // Jika scroll menargetkan sekutu, highlight sekutu juga
                p2StatusTables[i].setColor(Color.BLUE); // Warna highlight untuk target sekutu
            }
        }

        updateScrollUI();

        boolean isAwaitingInput = currentState == BattleState.AWAITING_INPUT;
        boolean heroSelected = currentPlayer.getActiveHero() != null;
        boolean hasScrolls = !currentPlayer.getScrolls().isEmpty();

        // Mengatur disabled state tombol
        attackButton.setDisabled(!(isAwaitingInput && heroSelected && !actionWasTaken));
        skillButton.setDisabled(!(isAwaitingInput && heroSelected && !actionWasTaken));
        // Perbaikan: useScrollButton harus dapat diakses jika sedang SELECTING_SCROLL dan belum menggunakan scroll
        useScrollButton.setDisabled(!(isAwaitingInput && hasScrolls && !scrollWasUsedThisTurn && !actionWasTaken) && currentState != BattleState.SELECTING_SCROLL);
        endTurnButton.setDisabled(!isAwaitingInput && currentState != BattleState.SELECTING_SCROLL); // Perbaikan: Tombol End Turn/Cancel harus aktif saat SELECTING_SCROLL


        if (currentState == BattleState.SELECTING_SCROLL) {
            endTurnButton.setText("Cancel");
            endTurnButton.setDisabled(false);
        } else {
            endTurnButton.setText("End Turn");
        }
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
                        if (crit != null) crit.play(0.2f);
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

    @Override
    public void show() {
        if (battleMusic != null) {
            battleMusic.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if(currentState == BattleState.SCROLL_DRAFT){
            setupDraftUI();
        } else {
            setupUI(); // Memanggil setupUI untuk mengatur ulang UI setelah resize
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override
    public void hide() {
        if (battleMusic != null) {
            battleMusic.stop();
        }
    }

    @Override public void dispose() {
        stage.dispose();
        if (background != null) background.dispose();
        if (skin != null) skin.dispose();
        if (crit != null) crit.dispose();
        if (battleMusic != null) battleMusic.dispose(); // Membersihkan musik

        if (statusEffectIcons != null) {
            for (Texture texture : statusEffectIcons.values()) {
                if (texture != null) texture.dispose();
            }
        }

        // Dispose scrolls from all lists to prevent memory leaks
        if (allPossibleScrolls != null) {
            for(Scroll s : allPossibleScrolls) s.dispose();
        }
        if(draftableScrolls != null) {
            for(Scroll s : draftableScrolls) s.dispose();
        }
        if (player1 != null && player1.getScrolls() != null) {
            for(Scroll s : player1.getScrolls()) s.dispose();
        }
        if (player2 != null && player2.getScrolls() != null) {
            for(Scroll s : player2.getScrolls()) s.dispose();
        }
    }
}
