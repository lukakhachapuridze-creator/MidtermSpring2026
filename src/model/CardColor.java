package model;

public enum CardColor {
    R,
    Y,
    G,
    B,
    NONE;

    public String getCode() {
        if (this == NONE) {
            return "";
        }
        return name();
    }

    public static CardColor fromCard(String card) {
        if (card.startsWith("R")) {
            return R;
        }
        if (card.startsWith("Y")) {
            return Y;
        }
        if (card.startsWith("G")) {
            return G;
        }
        if (card.startsWith("B")) {
            return B;
        }
        return NONE;
    }

    public static CardColor fromCode(String code) {
        if (code.equals("R")) {
            return R;
        }
        if (code.equals("Y")) {
            return Y;
        }
        if (code.equals("G")) {
            return G;
        }
        if (code.equals("B")) {
            return B;
        }
        return NONE;
    }
}
