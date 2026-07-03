import interfaces.BotPlayer;
import interfaces.CardRules;
import interfaces.ConsoleInput;
import interfaces.Deck;
import interfaces.Game;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import persistence.GameRecord;
import persistence.PersistenceService;
import persistence.PlayerRecord;
import persistence.RoundRecord;
import persistence.TopScoreRow;
import persistence.WinCountRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.BotPlayerService;
import service.CardRulesService;
import service.ConsoleInputService;
import service.DeckService;
import model.GameOptions;
import service.GameService;

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
        int humans = 0;
        long seed = System.currentTimeMillis();
        boolean saveDb = true;
        GameOptions gameOptions = GameOptions.defaults();
        int targetScore = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--humans") && i + 1 < args.length) {
                humans = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--no-stack")) {
                gameOptions.stackDraws = false;
            } else if (args[i].equals("--no-challenge")) {
                gameOptions.w4Challenge = false;
            } else if (args[i].equals("--no-uno-penalty")) {
                gameOptions.unoPenalty = false;
            } else if (args[i].equals("--no-opening-effect")) {
                gameOptions.openingCardEffect = false;
            } else if (args[i].equals("--seven-zero")) {
                gameOptions.sevenZeroRule = true;
            } else if (args[i].equals("--jump-in")) {
                gameOptions.jumpIn = true;
            } else if (args[i].equals("--target") && i + 1 < args.length) {
                targetScore = Integer.parseInt(args[++i]);
                gameOptions.targetScore = targetScore;
            } else if (args[i].equals("--no-db")) {
                saveDb = false;
            } else if (args[i].equals("--history")) {
                showRecentRounds();
                return;
            } else if (args[i].equals("--recent-games")) {
                showRecentGames();
                return;
            } else if (args[i].equals("--wins")) {
                showWinCounts();
                return;
            } else if (args[i].equals("--top-scores")) {
                showTopScores();
                return;
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                printHelp();
                return;
            }
        }

        random = new Random(seed);
        setupPlayers(bots, human, humans);
        log.info("bots={}, games={}, human={}, quiet={}, seed={}", bots, games, human, quiet, seed);

        if (playerNames.size() < 2 || playerNames.size() > 4) {
            log.warn("need 2 to 4 players, got {}", playerNames.size());
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        PersistenceService store = saveDb ? new PersistenceService() : null;

        try {
            Long gameId = null;
            if (store != null) {
                gameId = store.startGame(seed, playerNames, humanPlayers);
            }

            for (int g = 1; g <= games; g++) {
                log.info("game {} of {}", g, games);
                if (!quiet) {
                    System.out.println("\n=== Game " + g + " ===");
                }
                Game game = new GameService(playerNames, humanPlayers, hands, scores, random, quiet, scanner,
                        cardRules, deck, bot, console, gameOptions);
                game.play();
                if (store != null && game.hasWinner()) {
                    store.saveRound(gameId, g, game.winnerName(), game.winnerPoints(), playerNames, humanPlayers);
                }
                if (targetScore > 0 && hasTargetWinner(targetScore)) {
                    if (!quiet) {
                        System.out.println("Target score " + targetScore + " reached.");
                    }
                    break;
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

    static void showRecentRounds() {
        PersistenceService store = new PersistenceService();
        try {
            List<RoundRecord> rounds = store.recentRounds(10);
            if (rounds.isEmpty()) {
                System.out.println("No saved rounds yet.");
                return;
            }
            System.out.println("Recent rounds:");
            for (RoundRecord round : rounds) {
                System.out.println("round " + round.getRoundNumber()
                        + " game " + round.getGame().getId()
                        + " seed " + round.getGame().getSeed()
                        + " winner " + round.getWinner().getName()
                        + " +" + round.getWinnerPoints());
            }
            printStandings(store);
        } finally {
            store.close();
        }
    }

    static void showRecentGames() {
        PersistenceService store = new PersistenceService();
        try {
            List<GameRecord> games = store.recentGames(10);
            if (games.isEmpty()) {
                System.out.println("No saved games yet.");
                return;
            }
            System.out.println("Recent games:");
            for (GameRecord game : games) {
                System.out.println("game " + game.getId()
                        + " seed " + game.getSeed()
                        + " players " + game.getPlayerCount()
                        + " rounds " + game.getRounds().size());
            }
        } finally {
            store.close();
        }
    }

    static void showWinCounts() {
        PersistenceService store = new PersistenceService();
        try {
            List<WinCountRow> rows = store.playerWinCounts();
            if (rows.isEmpty()) {
                System.out.println("No wins recorded yet.");
                return;
            }
            System.out.println("Win counts:");
            for (WinCountRow row : rows) {
                System.out.println(row.getName() + ": " + row.getWins());
            }
        } finally {
            store.close();
        }
    }

    static void showTopScores() {
        PersistenceService store = new PersistenceService();
        try {
            List<TopScoreRow> rows = store.highestScores(10);
            if (rows.isEmpty()) {
                System.out.println("No scores recorded yet.");
                return;
            }
            System.out.println("Top scores:");
            for (TopScoreRow row : rows) {
                System.out.println(row.getName() + ": " + row.getTotalScore());
            }
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
            System.out.println(row.getName() + ": " + row.getTotalScore() + " (" + row.getWins() + " wins)");
        }
    }

    static void printHelp() {
        System.out.println("Usage: [--bots N] [--games N] [--human] [--humans N] [--quiet] [--seed N] [--no-db]");
        System.out.println("       [--target N] [--no-stack] [--no-challenge] [--no-uno-penalty]");
        System.out.println("       [--no-opening-effect] [--seven-zero] [--jump-in]");
        System.out.println("Reports: [--history] [--recent-games] [--wins] [--top-scores]");
    }

    static boolean hasTargetWinner(int target) {
        for (int i = 0; i < playerNames.size(); i++) {
            if (scores[i] >= target) {
                return true;
            }
        }
        return false;
    }

    static void setupPlayers(int bots, boolean human, int humans) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        if (human && humans == 0) {
            humans = 1;
        }
        for (int i = 0; i < humans; i++) {
            playerNames.add(i == 0 ? "You" : "Human" + (i + 1));
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
