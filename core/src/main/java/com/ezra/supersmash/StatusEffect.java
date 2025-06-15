package com.ezra.supersmash;

public abstract class StatusEffect {
    protected String name;
    protected int duration;

    public StatusEffect(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public void onApply(Hero target) { }

    public abstract void onTurnEnd(Hero target);

    public void decrementDuration() {
        this.duration--;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }
}
