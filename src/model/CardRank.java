package model;

public enum CardRank {
    WILD,
    WILD_DRAW_FOUR,
    SKIP,
    REVERSE,
    DRAW_TWO,
    NUMBER;

    public static CardRank fromCard(String card) {
        if (card.equals("W")) {
            return WILD;
        }
        if (card.equals("W4")) {
            return WILD_DRAW_FOUR;
        }
        if (card.endsWith("S")) {
            return SKIP;
        }
        if (card.endsWith("R")) {
            return REVERSE;
        }
        if (card.endsWith("+2")) {
            return DRAW_TWO;
        }
        return NUMBER;
    }
}
