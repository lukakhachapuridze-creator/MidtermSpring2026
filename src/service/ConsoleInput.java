import java.util.ArrayList;
import java.util.Scanner;

public interface ConsoleInput {
    int askCard(Scanner scanner, ArrayList<String> hand, String upCard, String calledColor);

    String askColor(Scanner scanner);
}
