package interfaces;

import java.util.ArrayList;
import java.util.Random;

public interface Deck {
    ArrayList<String> newShuffledDeck(Random random);

    String draw(ArrayList<String> deck, ArrayList<String> discard, Random random);
}
