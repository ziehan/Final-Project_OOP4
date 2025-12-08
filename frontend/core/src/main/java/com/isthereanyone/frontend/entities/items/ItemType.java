package com.isthereanyone.frontend.entities.items;

public enum ItemType {
    CANDLE,
    DOLL,
    DAGGER,
    BOWL,
    FLOWER;

    public static ItemType getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
