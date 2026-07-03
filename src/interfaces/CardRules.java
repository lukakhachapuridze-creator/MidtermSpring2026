package interfaces;

import java.util.List;
import model.CardColor;
import model.CardRank;

public interface CardRules {
    boolean isLegal(String card, String up, String call);

    boolean isLegal(String card, String up, String call, List<String> hand);

    boolean hasMatchingColor(List<String> hand, String up, String call);

    boolean canStackDraw(String card, int pendingDraws, CardRank pendingDrawRank);

    boolean hasLegalPlay(List<String> hand, String up, String call, int pendingDraws, CardRank pendingDrawRank);

    String color(String card);

    CardColor colorOf(String card);

    String rank(String card);

    CardRank rankOf(String card);

    int number(String card);

    int points(String card);

    boolean isJumpInMatch(String card, String up);

    boolean isSeven(String card);

    boolean isZero(String card);
}
