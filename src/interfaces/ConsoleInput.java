package interfaces;

import java.util.ArrayList;
import java.util.Scanner;
import model.CardRank;

public interface ConsoleInput {
    int askCard(Scanner scanner, ArrayList<String> hand, String upCard, String calledColor,
                int pendingDraws, CardRank pendingDrawRank);

    String askColor(Scanner scanner);

    boolean askUno(Scanner scanner);

    boolean askChallenge(Scanner scanner);

    int askSwapTarget(Scanner scanner, ArrayList<String> playerNames, int currentPlayer);

    boolean askJumpIn(Scanner scanner, String upCard);
}
