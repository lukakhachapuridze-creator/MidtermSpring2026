package service;

import interfaces.CardRules;
import interfaces.ConsoleInput;
import model.CardColor;
import model.CardRank;
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleInputService implements ConsoleInput {
    CardRules rules;

    public ConsoleInputService(CardRules rules) {
        this.rules = rules;
    }

    public int askCard(Scanner scanner, ArrayList<String> hand, String upCard, String calledColor,
                       int pendingDraws, CardRank pendingDrawRank) {
        while (true) {
            if (pendingDraws > 0) {
                System.out.print("Stack draw card or type draw to take " + pendingDraws + ": ");
            } else {
                System.out.print("Choose card index/code or draw: ");
            }
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("DRAW")) {
                return -1;
            }
            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    if (isAllowed(hand.get(index), upCard, calledColor, hand, pendingDraws, pendingDrawRank)) {
                        return index;
                    }
                    System.out.println("That card is not legal.");
                    continue;
                }
            } catch (Exception ignored) {
            }
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)) {
                    if (isAllowed(hand.get(i), upCard, calledColor, hand, pendingDraws, pendingDrawRank)) {
                        return i;
                    }
                    System.out.println("That card is not legal.");
                }
            }
            System.out.println("Card not found.");
        }
    }

    public String askColor(Scanner scanner) {
        while (true) {
            System.out.print("Call color R/Y/G/B: ");
            String input = scanner.nextLine().trim().toUpperCase();
            CardColor color = CardColor.fromCode(input);
            if (color != CardColor.NONE) {
                return color.getCode();
            }
            System.out.println("Bad color.");
        }
    }

    public boolean askUno(Scanner scanner) {
        System.out.print("Type uno: ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("uno");
    }

    public boolean askChallenge(Scanner scanner) {
        System.out.print("Challenge wild draw four? y/n: ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    public int askSwapTarget(Scanner scanner, ArrayList<String> playerNames, int currentPlayer) {
        while (true) {
            System.out.print("Swap hands with player index: ");
            String input = scanner.nextLine().trim();
            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < playerNames.size() && index != currentPlayer) {
                    return index;
                }
            } catch (Exception ignored) {
            }
            System.out.println("Bad player index.");
        }
    }

    public boolean askJumpIn(Scanner scanner, String upCard) {
        System.out.print("Jump in with " + upCard + "? y/n: ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    boolean isAllowed(String card, String upCard, String calledColor, ArrayList<String> hand,
                      int pendingDraws, CardRank pendingDrawRank) {
        if (pendingDraws > 0) {
            return rules.canStackDraw(card, pendingDraws, pendingDrawRank);
        }
        return rules.isLegal(card, upCard, calledColor, hand);
    }
}
