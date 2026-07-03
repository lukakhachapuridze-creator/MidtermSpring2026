import interfaces.BotPlayer;
import interfaces.CardRules;
import interfaces.Deck;
import model.CardRank;
import java.util.ArrayList;
import java.util.List;
import service.BotPlayerService;
import service.CardRulesService;
import service.DeckService;
import java.util.ArrayList;

public class CharacterizationTest {
    static CardRules rules = new CardRulesService();
    static Deck deck = new DeckService();
    static BotPlayer bot = new BotPlayerService(rules);

    public static void main(String[] args) {
        int passed = 0;
        int total = 0;

        total++;
        if (rules.color("R5").equals("R")) passed++;
        else fail("color match");

        total++;
        if (rules.rank("G+2").equals("DRAW_TWO")) passed++;
        else fail("draw two rank");

        total++;
        if (rules.rank("YS").equals("SKIP")) passed++;
        else fail("skip rank");

        total++;
        if (rules.rank("BR").equals("REVERSE")) passed++;
        else fail("reverse rank");

        total++;
        if (rules.rank("W").equals("WILD") && rules.rank("W4").equals("WILD_DRAW_FOUR")) passed++;
        else fail("wild ranks");

        total++;
        if (rules.rankOf("G+2") == CardRank.DRAW_TWO) passed++;
        else fail("draw two enum");

        total++;
        if (rules.isLegal("R2", "R9", "")) passed++;
        else fail("same color");

        total++;
        if (rules.isLegal("G9", "R9", "")) passed++;
        else fail("same number");

        total++;
        if (rules.isLegal("YS", "RS", "")) passed++;
        else fail("same action type skip");

        total++;
        if (rules.isLegal("B3", "W", "B")) passed++;
        else fail("called color after wild");

        total++;
        if (rules.isLegal("W", "R9", "")) passed++;
        else fail("wild always legal");

        total++;
        if (rules.isLegal("W4", "G5", "")) passed++;
        else fail("wild draw four always legal");

        total++;
        if (!rules.isLegal("B3", "R9", "")) passed++;
        else fail("illegal card");

        total++;
        if (rules.points("W4") == 50 && rules.points("R5") == 5 && rules.points("YS") == 20) passed++;
        else fail("scoring");

        total++;
        if (rules.number("Y7") == 7) passed++;
        else fail("number parse");

        ArrayList<String> hand = new ArrayList<String>();
        hand.add("B3");
        hand.add("R4");
        hand.add("W");
        total++;
        if (bot.chooseCard(hand, "R9", "", 0, null) == 1) passed++;
        else fail("bot prefers number over wild");

        hand.clear();
        hand.add("R+2");
        hand.add("R4");
        total++;
        if (bot.chooseCard(hand, "R9", "", 0, null) == 0) passed++;
        else fail("bot prefers draw two");

        hand.clear();
        hand.add("B1");
        hand.add("B2");
        hand.add("R3");
        total++;
        if (bot.chooseColor(hand).equals("B")) passed++;
        else fail("bot color pick");

        ArrayList<String> deckPile = new ArrayList<String>();
        ArrayList<String> discard = new ArrayList<String>();
        deckPile.add("Y1");
        discard.add("R2");
        discard.add("G3");
        total++;
        if (deck.draw(deckPile, discard, new java.util.Random(1)).equals("Y1") && deckPile.size() == 0 && discard.size() == 2) passed++;
        else fail("draw reshuffles discard");

        total++;
        if (!rules.isLegal("R5", "W", "")) passed++;
        else fail("wild top needs called color for color cards");

        total++;
        List<String> w4Hand = new ArrayList<String>();
        w4Hand.add("G2");
        w4Hand.add("W4");
        if (!rules.isLegal("W4", "G9", "", w4Hand)) passed++;
        else fail("wild draw four needs no color match");

        total++;
        if (rules.canStackDraw("R+2", 2, CardRank.DRAW_TWO)) passed++;
        else fail("draw two stack");

        total++;
        if (rules.canStackDraw("W4", 2, CardRank.DRAW_TWO)) passed++;
        else fail("wild four stack on draw two");

        total++;
        if (rules.isJumpInMatch("Y3", "Y3") && !rules.isJumpInMatch("Y3", "R3")) passed++;
        else fail("jump in match");

        total++;
        if (rules.isSeven("R7") && rules.isZero("B0")) passed++;
        else fail("seven zero detect");

        System.out.println("Passed " + passed + " of " + total + " characterization checks.");
        if (passed != total) {
            System.exit(1);
        }
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}
