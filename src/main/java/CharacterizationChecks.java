import java.util.ArrayList;

public class CharacterizationChecks {
    static CardRules rules = new CardRulesService();
    static Deck deck = new DeckService();
    static BotPlayer bot = new BotPlayerService(rules);

    public static void runAll() {
        colorMatch();
        drawTwoRank();
        skipRank();
        reverseRank();
        wildRanks();
        drawTwoEnum();
        sameColor();
        sameNumber();
        sameActionTypeSkip();
        calledColorAfterWild();
        wildAlwaysLegal();
        wildDrawFourAlwaysLegal();
        illegalCard();
        scoring();
        numberParse();
        botPrefersNumberOverWild();
        botPrefersDrawTwo();
        botColorPick();
        drawReshufflesDiscard();
        wildTopNeedsCalledColor();
        System.out.println("Passed 20 of 20 characterization checks.");
    }

    public static void colorMatch() {
        check(rules.color("R5").equals("R"), "color match");
    }

    public static void drawTwoRank() {
        check(rules.rank("G+2").equals("DRAW_TWO"), "draw two rank");
    }

    public static void skipRank() {
        check(rules.rank("YS").equals("SKIP"), "skip rank");
    }

    public static void reverseRank() {
        check(rules.rank("BR").equals("REVERSE"), "reverse rank");
    }

    public static void wildRanks() {
        check(rules.rank("W").equals("WILD") && rules.rank("W4").equals("WILD_DRAW_FOUR"), "wild ranks");
    }

    public static void drawTwoEnum() {
        check(rules.rankOf("G+2") == CardRank.DRAW_TWO, "draw two enum");
    }

    public static void sameColor() {
        check(rules.isLegal("R2", "R9", ""), "same color");
    }

    public static void sameNumber() {
        check(rules.isLegal("G9", "R9", ""), "same number");
    }

    public static void sameActionTypeSkip() {
        check(rules.isLegal("YS", "RS", ""), "same action type skip");
    }

    public static void calledColorAfterWild() {
        check(rules.isLegal("B3", "W", "B"), "called color after wild");
    }

    public static void wildAlwaysLegal() {
        check(rules.isLegal("W", "R9", ""), "wild always legal");
    }

    public static void wildDrawFourAlwaysLegal() {
        check(rules.isLegal("W4", "G5", ""), "wild draw four always legal");
    }

    public static void illegalCard() {
        check(!rules.isLegal("B3", "R9", ""), "illegal card");
    }

    public static void scoring() {
        check(rules.points("W4") == 50 && rules.points("R5") == 5 && rules.points("YS") == 20, "scoring");
    }

    public static void numberParse() {
        check(rules.number("Y7") == 7, "number parse");
    }

    public static void botPrefersNumberOverWild() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B3");
        hand.add("R4");
        hand.add("W");
        check(bot.chooseCard(hand, "R9", "") == 1, "bot prefers number over wild");
    }

    public static void botPrefersDrawTwo() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("R+2");
        hand.add("R4");
        check(bot.chooseCard(hand, "R9", "") == 0, "bot prefers draw two");
    }

    public static void botColorPick() {
        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B1");
        hand.add("B2");
        hand.add("R3");
        check(bot.chooseColor(hand).equals("B"), "bot color pick");
    }

    public static void drawReshufflesDiscard() {
        ArrayList<String> deckPile = new ArrayList<String>();
        ArrayList<String> discard = new ArrayList<String>();
        deckPile.add("Y1");
        discard.add("R2");
        discard.add("G3");
        check(deck.draw(deckPile, discard, new java.util.Random(1)).equals("Y1")
                && deckPile.size() == 0 && discard.size() == 2, "draw reshuffles discard");
    }

    public static void wildTopNeedsCalledColor() {
        check(!rules.isLegal("R5", "W", ""), "wild top needs called color for color cards");
    }

    static void check(boolean ok, String name) {
        if (!ok) {
            throw new RuntimeException("Failed: " + name);
        }
    }
}
