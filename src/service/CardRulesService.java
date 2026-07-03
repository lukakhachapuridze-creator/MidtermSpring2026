package service;

import interfaces.CardRules;
import java.util.List;
import model.CardColor;
import model.CardRank;

public class CardRulesService implements CardRules {
    public boolean isLegal(String card, String up, String call) {
        return isLegal(card, up, call, null);
    }

    public boolean isLegal(String card, String up, String call, List<String> hand) {
        if (card.equals("W4") && hand != null && hasMatchingColor(hand, up, call)) {
            return false;
        }
        if (card.startsWith("W")) {
            return true;
        }
        if (color(card).equals(color(up))) {
            return true;
        }
        if (!call.equals("") && color(card).equals(call)) {
            return true;
        }
        CardRank cardRank = rankOf(card);
        CardRank upRank = rankOf(up);
        if (cardRank == upRank && cardRank != CardRank.NUMBER) {
            return true;
        }
        if (cardRank == CardRank.NUMBER && upRank == CardRank.NUMBER && number(card) == number(up)) {
            return true;
        }
        return false;
    }

    public boolean hasMatchingColor(List<String> hand, String up, String call) {
        String matchColor = !call.equals("") ? call : color(up);
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (card.startsWith("W")) {
                continue;
            }
            if (color(card).equals(matchColor)) {
                return true;
            }
        }
        return false;
    }

    public boolean canStackDraw(String card, int pendingDraws, CardRank pendingDrawRank) {
        if (pendingDraws <= 0) {
            return false;
        }
        CardRank rank = rankOf(card);
        if (rank == CardRank.DRAW_TWO && pendingDrawRank == CardRank.DRAW_TWO) {
            return true;
        }
        if (rank == CardRank.WILD_DRAW_FOUR) {
            return pendingDrawRank == CardRank.DRAW_TWO || pendingDrawRank == CardRank.WILD_DRAW_FOUR;
        }
        return false;
    }

    public boolean hasLegalPlay(List<String> hand, String up, String call, int pendingDraws, CardRank pendingDrawRank) {
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (pendingDraws > 0) {
                if (canStackDraw(card, pendingDraws, pendingDrawRank)) {
                    return true;
                }
            } else if (isLegal(card, up, call, hand)) {
                return true;
            }
        }
        return false;
    }

    public String color(String card) {
        return colorOf(card).getCode();
    }

    public CardColor colorOf(String card) {
        return CardColor.fromCard(card);
    }

    public String rank(String card) {
        return rankOf(card).name();
    }

    public CardRank rankOf(String card) {
        return CardRank.fromCard(card);
    }

    public int number(String card) {
        if (rankOf(card) == CardRank.NUMBER) {
            return Integer.parseInt(card.substring(1));
        }
        return -1;
    }

    public int points(String card) {
        CardRank r = rankOf(card);
        if (r == CardRank.NUMBER) {
            return number(card);
        }
        if (r == CardRank.SKIP || r == CardRank.REVERSE || r == CardRank.DRAW_TWO) {
            return 20;
        }
        if (r == CardRank.WILD || r == CardRank.WILD_DRAW_FOUR) {
            return 50;
        }
        return 0;
    }

    public boolean isJumpInMatch(String card, String up) {
        return !up.startsWith("W") && card.equals(up);
    }

    public boolean isSeven(String card) {
        return rankOf(card) == CardRank.NUMBER && number(card) == 7;
    }

    public boolean isZero(String card) {
        return rankOf(card) == CardRank.NUMBER && number(card) == 0;
    }
}
