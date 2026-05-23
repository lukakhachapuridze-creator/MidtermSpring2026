import java.util.ArrayList;

public class BotPlayer {
    public static int chooseCard(ArrayList<String> hand, String upCard, String calledColor) {
        String[] prefer = {"DRAW_TWO", "SKIP", "NUMBER"};
        for (int p = 0; p < prefer.length; p++) {
            for (int i = 0; i < hand.size(); i++) {
                String card = hand.get(i);
                if (CardRules.rank(card).equals(prefer[p]) && CardRules.isLegal(card, upCard, calledColor)) {
                    return i;
                }
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).startsWith("W")) {
                return i;
            }
        }
        return -1;
    }

    public static String chooseColor(ArrayList<String> hand) {
        int r = 0;
        int y = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < hand.size(); i++) {
            String c = CardRules.color(hand.get(i));
            if (c.equals("R")) {
                r++;
            } else if (c.equals("Y")) {
                y++;
            } else if (c.equals("G")) {
                g++;
            } else if (c.equals("B")) {
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
}
