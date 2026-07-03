package model;

public class GameOptions {
    public boolean stackDraws = true;
    public boolean w4Challenge = true;
    public boolean w4OnlyWithoutColor = true;
    public boolean unoPenalty = true;
    public int targetScore = 0;
    public boolean openingCardEffect = true;
    public boolean sevenZeroRule = false;
    public boolean jumpIn = false;

    public static GameOptions defaults() {
        return new GameOptions();
    }
}
