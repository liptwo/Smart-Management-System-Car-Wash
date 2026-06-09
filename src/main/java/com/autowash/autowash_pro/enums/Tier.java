package com.autowash.autowash_pro.enums;

public enum Tier {
    MEMBER, SILVER, GOLD, PLATINUM;

    public int getBookingWindowDays() {
        return switch (this) {
            case MEMBER   -> 7;
            case SILVER   -> 10;
            case GOLD     -> 12;
            case PLATINUM -> 14;
        };
    }

    public int getPriorityScore() {
        return switch (this) {
            case MEMBER   -> 10;
            case SILVER   -> 20;
            case GOLD     -> 30;
            case PLATINUM -> 40;
        };
    }

    public double getPointMultiplier() {
        return switch (this) {
            case MEMBER   -> 1.0;
            case SILVER   -> 1.05;
            case GOLD     -> 1.10;
            case PLATINUM -> 1.15;
        };
    }
}
