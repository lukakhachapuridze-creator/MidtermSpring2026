package service;

import interfaces.BotPlayer;
import interfaces.CardRules;
import model.CardColor;
import model.CardRank;
import java.util.ArrayList;

public class BotPlayerService implements BotPlayer {
    CardRules rules;

    public BotPlayerService(CardRules rules) {
        this.rules = rules;
    }

    public int chooseCard(ArrayList<String> hand, String upCard, String calledColor,
                          int pendingDraws, CardRank pendingDrawRank) {
        if (pendingDraws > 0) {
            for (int i = 0; i < hand.size(); i++) {
                if (rules.canStackDraw(hand.get(i), pendingDraws, pendingDrawRank)) {
                    return i;
                }
            }
            return -1;
        }

        CardRank[] prefer = {CardRank.DRAW_TWO, CardRank.SKIP, CardRank.NUMBER};
        for (int p = 0; p < prefer.length; p++) {
            for (int i = 0; i < hand.size(); i++) {
                String card = hand.get(i);
                if (rules.rankOf(card) == prefer[p] && rules.isLegal(card, upCard, calledColor, hand)) {
                    return i;
                }
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (card.startsWith("W") && rules.isLegal(card, upCard, calledColor, hand)) {
                return i;
            }
        }
        return -1;
    }

    public String chooseColor(ArrayList<String> hand) {
        int r = 0;
        int y = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < hand.size(); i++) {
            CardColor c = rules.colorOf(hand.get(i));
            if (c == CardColor.R) {
                r++;
            } else if (c == CardColor.Y) {
                y++;
            } else if (c == CardColor.G) {
                g++;
            } else if (c == CardColor.B) {
                b++;
            }
        }
        if (r >= y && r >= g && r >= b) {
            return "R";
        } else if (y >= r && y >= g && y >= b) {
            return "Y";
        } else if (g >= r && g >= y && g >= b) {
            return "G";
        } else {
            return "B";
        }
    }

    public boolean willChallenge() {
        return false;
    }

    public int chooseSwapTarget(ArrayList<ArrayList<String>> hands, int currentPlayer) {
        int best = -1;
        int bestSize = -1;
        for (int i = 0; i < hands.size(); i++) {
            if (i == currentPlayer) {
                continue;
            }
            int size = hands.get(i).size();
            if (size > bestSize) {
                bestSize = size;
                best = i;
            }
        }
        return best;
    }

    public int findJumpInIndex(ArrayList<String> hand, String upCard) {
        for (int i = 0; i < hand.size(); i++) {
            if (rules.isJumpInMatch(hand.get(i), upCard)) {
                return i;
            }
        }
        return -1;
    }
}
