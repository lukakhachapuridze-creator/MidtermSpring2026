import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DeckService implements Deck {
    public ArrayList<String> newShuffledDeck(Random random) {
        ArrayList<String> deck = new ArrayList<String>();
        CardColor[] colors = {CardColor.R, CardColor.Y, CardColor.G, CardColor.B};
        for (int c = 0; c < colors.length; c++) {
            String code = colors[c].getCode();
            deck.add(code + "0");
            for (int n = 1; n <= 9; n++) {
                deck.add(code + n);
                deck.add(code + n);
            }
            deck.add(code + "S");
            deck.add(code + "S");
            deck.add(code + "R");
            deck.add(code + "R");
            deck.add(code + "+2");
            deck.add(code + "+2");
        }
        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }
        Collections.shuffle(deck, random);
        return deck;
    }

    public String draw(ArrayList<String> deck, ArrayList<String> discard, Random random) {
        if (deck.size() == 0) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }
        if (deck.size() == 0) {
            return "W";
        }
        return deck.remove(0);
    }
}
