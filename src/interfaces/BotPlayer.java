package interfaces;

import java.util.ArrayList;
import model.CardRank;

public interface BotPlayer {
    int chooseCard(ArrayList<String> hand, String upCard, String calledColor,
                   int pendingDraws, CardRank pendingDrawRank);

    String chooseColor(ArrayList<String> hand);

    boolean willChallenge();

    int chooseSwapTarget(ArrayList<ArrayList<String>> hands, int currentPlayer);

    int findJumpInIndex(ArrayList<String> hand, String upCard);
}
