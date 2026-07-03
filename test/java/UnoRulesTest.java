import model.CardRank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.BotPlayerService;
import service.CardRulesService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnoRulesTest {
    CardRulesService rules;
    BotPlayerService bot;

    @BeforeEach
    void setUp() {
        rules = new CardRulesService();
        bot = new BotPlayerService(rules);
    }

    @Test
    void wildDrawFourIllegalWhenColorAvailable() {
        List<String> hand = Arrays.asList("G2", "W4");
        assertFalse(rules.isLegal("W4", "G9", "", hand));
        assertTrue(rules.isLegal("W4", "G9", "", null));
        List<String> noColor = Arrays.asList("R5", "W4");
        assertTrue(rules.isLegal("W4", "G9", "", noColor));
    }

    @Test
    void wildDrawFourLegalWhenOnlyWildsMatch() {
        List<String> hand = Arrays.asList("W", "W4");
        assertTrue(rules.isLegal("W4", "R3", "", hand));
    }

    @Test
    void drawTwoStacksOnDrawTwo() {
        assertTrue(rules.canStackDraw("G+2", 2, CardRank.DRAW_TWO));
        assertFalse(rules.canStackDraw("R5", 2, CardRank.DRAW_TWO));
    }

    @Test
    void wildDrawFourStacksOnDrawTwoAndWildDrawFour() {
        assertTrue(rules.canStackDraw("W4", 2, CardRank.DRAW_TWO));
        assertTrue(rules.canStackDraw("W4", 4, CardRank.WILD_DRAW_FOUR));
        assertFalse(rules.canStackDraw("R+2", 4, CardRank.WILD_DRAW_FOUR));
    }

    @Test
    void hasMatchingColorUsesCalledColor() {
        List<String> hand = Arrays.asList("B2", "W4");
        assertTrue(rules.hasMatchingColor(hand, "W", "B"));
        assertFalse(rules.hasMatchingColor(hand, "W", "R"));
    }

    @Test
    void hasLegalPlayWithPendingDrawStack() {
        List<String> hand = Arrays.asList("R5", "Y+2");
        assertTrue(rules.hasLegalPlay(hand, "R+2", "", 2, CardRank.DRAW_TWO));
        List<String> noStack = Arrays.asList("R5", "G3");
        assertFalse(rules.hasLegalPlay(noStack, "R+2", "", 2, CardRank.DRAW_TWO));
    }

    @Test
    void botStacksDrawTwoWhenPending() {
        ArrayList<String> hand = new ArrayList<>(Arrays.asList("R5", "B+2"));
        int pick = bot.chooseCard(hand, "G+2", "", 2, CardRank.DRAW_TWO);
        assertEquals(1, pick);
    }

    @Test
    void botTakesDrawWhenCannotStack() {
        ArrayList<String> hand = new ArrayList<>(Arrays.asList("R5", "G3"));
        int pick = bot.chooseCard(hand, "Y+2", "", 2, CardRank.DRAW_TWO);
        assertEquals(-1, pick);
    }

    @Test
    void reverseMatchesReverse() {
        assertTrue(rules.isLegal("BR", "YR", ""));
    }

    @Test
    void drawTwoMatchesDrawTwo() {
        assertTrue(rules.isLegal("B+2", "G+2", ""));
    }

    @Test
    void zeroCardPoints() {
        assertEquals(0, rules.points("Y0"));
    }

    @Test
    void jumpInRequiresExactMatch() {
        assertTrue(rules.isJumpInMatch("R5", "R5"));
        assertFalse(rules.isJumpInMatch("R5", "R6"));
        assertFalse(rules.isJumpInMatch("R5", "G5"));
        assertFalse(rules.isJumpInMatch("W", "W"));
    }

    @Test
    void sevenAndZeroDetection() {
        assertTrue(rules.isSeven("B7"));
        assertFalse(rules.isSeven("B8"));
        assertTrue(rules.isZero("G0"));
        assertFalse(rules.isZero("G1"));
    }

    @Test
    void botFindsJumpInCard() {
        ArrayList<String> hand = new ArrayList<>(Arrays.asList("R4", "R5", "B2"));
        assertEquals(1, bot.findJumpInIndex(hand, "R5"));
        assertEquals(-1, bot.findJumpInIndex(hand, "R6"));
    }

    @Test
    void botPicksLargestHandForSevenSwap() {
        ArrayList<ArrayList<String>> all = new ArrayList<>();
        all.add(new ArrayList<>(Arrays.asList("R1", "R2")));
        all.add(new ArrayList<>(Arrays.asList("G1", "G2", "G3", "G4")));
        all.add(new ArrayList<>(Arrays.asList("B1")));
        assertEquals(1, bot.chooseSwapTarget(all, 0));
    }
}
