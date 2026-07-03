package service;

import interfaces.CardRules;
import interfaces.ConsoleInput;
import model.CardColor;
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleInputService implements ConsoleInput {
    CardRules rules;

    public ConsoleInputService(CardRules rules) {
        this.rules = rules;
    }

    public int askCard(Scanner scanner, ArrayList<String> hand, String upCard, String calledColor) {
        while (true) {
            System.out.print("Choose card index/code or draw: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("DRAW")) {
                return -1;
            }
            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    return index;
                }
            } catch (Exception ignored) {
            }
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)) {
                    if (rules.isLegal(hand.get(i), upCard, calledColor)) {
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
}
