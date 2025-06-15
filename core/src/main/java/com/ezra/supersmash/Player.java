package com.ezra.supersmash;

import java.util.Collections;
import java.util.List;

public class Player {
    private Hero activeHero;

    // TEMPORARY: Constructor takes only one hero
    public Player(Hero hero) {
        this.activeHero = hero;
    }

    public Hero getActiveHero() {
        return activeHero;
    }

    // setActiveHero is no longer needed for a 1v1 test

    // getHeroRoster now returns a list containing only the active hero
    public List<Hero> getHeroRoster() {
        return Collections.singletonList(activeHero);
    }

    // hasLost checks if the single hero is no longer alive
    public boolean hasLost() {
        return !activeHero.isAlive();
    }

    // --- Energy methods remain the same ---
    private int energy = 3;
    public int getEnergy() { return energy; }
    public void gainEnergy(int amount) { this.energy += amount; }
    public boolean spendEnergy(int amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }
}
