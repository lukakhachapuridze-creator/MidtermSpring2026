import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static ArrayList<String> playerNames = new ArrayList<String>();
    static ArrayList<Boolean> humanPlayers = new ArrayList<Boolean>();
    static ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();
    static int[] scores = new int[10];
    static boolean quiet = false;
    static Random random = new Random();
    static Scanner scanner = new Scanner(System.in);

    static CardRules cardRules = new CardRulesService();
    static Deck deck = new DeckService();
    static BotPlayer bot = new BotPlayerService(cardRules);
    static ConsoleInput console = new ConsoleInputService(cardRules);

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();
        boolean saveDb = true;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--no-db")) {
                saveDb = false;
            } else if (args[i].equals("--history")) {
                showHistory();
                return;
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N] [--no-db] [--history]");
                return;
            }
        }

        random = new Random(seed);
        setupPlayers(bots, human);
        log.info("bots={}, games={}, human={}, quiet={}, seed={}", bots, games, human, quiet, seed);

        if (playerNames.size() < 2 || playerNames.size() > 4) {
            log.warn("need 2 to 4 players, got {}", playerNames.size());
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        PersistenceService store = saveDb ? new PersistenceService() : null;

        try {
            for (int g = 1; g <= games; g++) {
                log.info("game {} of {}", g, games);
                if (!quiet) {
                    System.out.println("\n=== Game " + g + " ===");
                }
                Game game = new GameService(playerNames, humanPlayers, hands, scores, random, quiet, scanner,
                        cardRules, deck, bot, console);
                game.play();
                if (store != null && game.hasWinner()) {
                    store.saveWin(game.winnerName(), game.winnerPoints(), seed, g);
                }
            }

            System.out.println("\nFinal scores:");
            for (int i = 0; i < playerNames.size(); i++) {
                System.out.println(playerNames.get(i) + ": " + scores[i]);
                log.info("{} finished with {}", playerNames.get(i), scores[i]);
            }

            if (store != null) {
                printStandings(store);
            }
        } finally {
            if (store != null) {
                store.close();
            }
        }
    }

    static void showHistory() {
        PersistenceService store = new PersistenceService();
        try {
            List<GameRecord> games = store.recentGames(10);
            if (games.isEmpty()) {
                System.out.println("No saved games yet.");
                return;
            }
            System.out.println("Recent games:");
            for (GameRecord game : games) {
                System.out.println(game.getWinnerName() + " +" + game.getPoints()
                        + " (game " + game.getGameNumber() + ", seed " + game.getSeed() + ")");
            }
            printStandings(store);
        } finally {
            store.close();
        }
    }

    static void printStandings(PersistenceService store) {
        List<PlayerRecord> rows = store.standings();
        if (rows.isEmpty()) {
            return;
        }
        System.out.println("\nAll-time standings:");
        for (PlayerRecord row : rows) {
            System.out.println(row.getName() + ": " + row.getTotalScore());
        }
    }

    static void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<String>());
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<String>());
        }
    }

    static void selfTest() {
        CharacterizationTest.main(new String[0]);
    }
}
