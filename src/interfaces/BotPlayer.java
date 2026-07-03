package interfaces;

import java.util.ArrayList;

public interface BotPlayer {
    int chooseCard(ArrayList<String> hand, String upCard, String calledColor);

    String chooseColor(ArrayList<String> hand);
}
