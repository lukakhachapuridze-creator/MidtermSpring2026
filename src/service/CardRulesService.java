package service;

import interfaces.CardRules;
import model.CardColor;
import model.CardRank;

public class CardRulesService implements CardRules {
    public boolean isLegal(String card, String up, String call) {
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
}
