package com.ezra.supersmash;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Hero> heroRoster;
    private Hero activeHero;
    private String name;
    private List<Scroll> scrolls = new ArrayList<>(); // Tambahkan ini
    private static final int MAX_SCROLLS = 3; // Tambahkan ini

    public Player(String name, List<Hero> heroes) {
        if (heroes == null || heroes.isEmpty()) {
            throw new IllegalArgumentException("Player must have at least one hero.");
        }
        this.name = name;
        this.heroRoster = new ArrayList<>(heroes);
        // Awalnya tidak ada hero aktif, pemain harus memilih
        this.activeHero = null;
    }
    public List<Scroll> getScrolls() {
        return scrolls;
    }

    public boolean canAddScroll() {
        return scrolls.size() < MAX_SCROLLS;
    }

    public void addScroll(Scroll scroll) {
        if (canAddScroll()) {
            scrolls.add(scroll);
        }
    }

    public void removeScroll(Scroll scroll) {
        scrolls.remove(scroll);
        scroll.dispose(); // Hapus texture dari memori untuk mencegah memory leak
    }

    public Hero getActiveHero() {
        return activeHero;
    }


    public String getName() {
        return name;
    }

    public boolean setActiveHero(int heroIndex) {
        // -1 berarti tidak ada hero aktif yang dipilih
        if (heroIndex == -1) {
            this.activeHero = null;
            return true;
        }

        if (heroIndex >= 0 && heroIndex < heroRoster.size()) {
            Hero newActiveHero = heroRoster.get(heroIndex);
            if (newActiveHero.isAlive()) { // Boleh memilih hero yang sama lagi
                this.activeHero = newActiveHero;
                return true;
            }
        }
        return false;
    }

    public List<Hero> getHeroRoster() {
        return heroRoster;
    }

    public boolean hasLost() {
        for (Hero hero : heroRoster) {
            if (hero.isAlive()) {
                return false;
            }
        }
        return true;
    }
}
