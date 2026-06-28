import org.junit.jupiter.api.Test;

class CharacterizationTest {
    @Test
    void colorMatch() {
        CharacterizationChecks.colorMatch();
    }

    @Test
    void drawTwoRank() {
        CharacterizationChecks.drawTwoRank();
    }

    @Test
    void skipRank() {
        CharacterizationChecks.skipRank();
    }

    @Test
    void reverseRank() {
        CharacterizationChecks.reverseRank();
    }

    @Test
    void wildRanks() {
        CharacterizationChecks.wildRanks();
    }

    @Test
    void drawTwoEnum() {
        CharacterizationChecks.drawTwoEnum();
    }

    @Test
    void sameColor() {
        CharacterizationChecks.sameColor();
    }

    @Test
    void sameNumber() {
        CharacterizationChecks.sameNumber();
    }

    @Test
    void sameActionTypeSkip() {
        CharacterizationChecks.sameActionTypeSkip();
    }

    @Test
    void calledColorAfterWild() {
        CharacterizationChecks.calledColorAfterWild();
    }

    @Test
    void wildAlwaysLegal() {
        CharacterizationChecks.wildAlwaysLegal();
    }

    @Test
    void wildDrawFourAlwaysLegal() {
        CharacterizationChecks.wildDrawFourAlwaysLegal();
    }

    @Test
    void illegalCard() {
        CharacterizationChecks.illegalCard();
    }

    @Test
    void scoring() {
        CharacterizationChecks.scoring();
    }

    @Test
    void numberParse() {
        CharacterizationChecks.numberParse();
    }

    @Test
    void botPrefersNumberOverWild() {
        CharacterizationChecks.botPrefersNumberOverWild();
    }

    @Test
    void botPrefersDrawTwo() {
        CharacterizationChecks.botPrefersDrawTwo();
    }

    @Test
    void botColorPick() {
        CharacterizationChecks.botColorPick();
    }

    @Test
    void drawReshufflesDiscard() {
        CharacterizationChecks.drawReshufflesDiscard();
    }

    @Test
    void wildTopNeedsCalledColor() {
        CharacterizationChecks.wildTopNeedsCalledColor();
    }
}
