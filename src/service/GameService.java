package service;

import interfaces.BotPlayer;
import interfaces.CardRules;
import interfaces.ConsoleInput;
import interfaces.Deck;
import interfaces.Game;
import model.CardRank;
import model.GameOptions;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameService implements Game {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    ArrayList<String> playerNames;
    ArrayList<Boolean> humanPlayers;
    ArrayList<ArrayList<String>> hands;
    int[] scores;
    Random random;
    boolean quiet;
    Scanner scanner;
    CardRules rules;
    Deck deck;
    BotPlayer bot;
    ConsoleInput console;
    GameOptions options;

    ArrayList<String> deckCards;
    ArrayList<String> discard;
    int currentPlayer;
    int direction;
    String upCard;
    String calledColor;
    int pendingDraws;
    CardRank pendingDrawRank;
    ArrayList<Boolean> unoPenalty;
    int lastW4Player;
    boolean lastW4HadColorMatch;

    boolean hasWinner;
    String winnerName;
    int winnerPoints;

    public GameService(ArrayList<String> playerNames, ArrayList<Boolean> humanPlayers,
                       ArrayList<ArrayList<String>> hands, int[] scores,
                       Random random, boolean quiet, Scanner scanner,
                       CardRules rules, Deck deck, BotPlayer bot, ConsoleInput console) {
        this(playerNames, humanPlayers, hands, scores, random, quiet, scanner,
                rules, deck, bot, console, GameOptions.defaults());
    }

    public GameService(ArrayList<String> playerNames, ArrayList<Boolean> humanPlayers,
                       ArrayList<ArrayList<String>> hands, int[] scores,
                       Random random, boolean quiet, Scanner scanner,
                       CardRules rules, Deck deck, BotPlayer bot, ConsoleInput console,
                       GameOptions options) {
        this.playerNames = playerNames;
        this.humanPlayers = humanPlayers;
        this.hands = hands;
        this.scores = scores;
        this.random = random;
        this.quiet = quiet;
        this.scanner = scanner;
        this.rules = rules;
        this.deck = deck;
        this.bot = bot;
        this.console = console;
        this.options = options;
        this.unoPenalty = new ArrayList<Boolean>();
        for (int i = 0; i < playerNames.size(); i++) {
            unoPenalty.add(Boolean.FALSE);
        }
    }

    public void play() {
        hasWinner = false;
        winnerName = "";
        winnerPoints = 0;
        pendingDraws = 0;
        pendingDrawRank = null;
        deckCards = deck.newShuffledDeck(random);
        discard = new ArrayList<String>();
        for (int i = 0; i < hands.size(); i++) {
            hands.get(i).clear();
        }
        for (int i = 0; i < playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                hands.get(i).add(draw());
            }
        }
        upCard = draw();
        while (upCard.startsWith("W")) {
            discard.add(upCard);
            upCard = draw();
        }
        calledColor = "";
        direction = 1;
        currentPlayer = random.nextInt(playerNames.size());
        log.info("up card {}, {} goes first", upCard, playerNames.get(currentPlayer));

        if (options.openingCardEffect) {
            applyOpeningCardEffect(upCard);
        }

        int guard = 0;
        while (guard < 3000) {
            guard++;

            if (tryJumpIn()) {
                if (hasWinner) {
                    return;
                }
                continue;
            }

            String name = playerNames.get(currentPlayer);
            ArrayList<String> hand = hands.get(currentPlayer);

            if (unoPenalty.get(currentPlayer).booleanValue() && options.unoPenalty) {
                hand.add(draw());
                hand.add(draw());
                unoPenalty.set(currentPlayer, Boolean.FALSE);
                if (!quiet) {
                    System.out.println(name + " draws 2 for missing uno.");
                }
            }

            if (!quiet) {
                System.out.println("\nUp card: " + upCard + colorSuffix()
                        + "  direction: " + directionLabel());
                System.out.println(name + " hand: " + join(hand));
                if (pendingDraws > 0) {
                    System.out.println("Pending draw: " + pendingDraws);
                }
            }

            int chosen = -1;
            if (humanPlayers.get(currentPlayer).booleanValue()) {
                chosen = console.askCard(scanner, hand, upCard, calledColor, pendingDraws, pendingDrawRank);
            } else {
                chosen = bot.chooseCard(hand, upCard, calledColor, pendingDraws, pendingDrawRank);
            }

            if (chosen == -1) {
                if (pendingDraws > 0) {
                    drawCards(hand, pendingDraws);
                    if (!quiet) {
                        System.out.println(name + " draws " + pendingDraws);
                    }
                    pendingDraws = 0;
                    pendingDrawRank = null;
                    next();
                    continue;
                }
                String drawn = draw();
                hand.add(drawn);
                if (!quiet) {
                    System.out.println(name + " draws " + drawn);
                }
                if (rules.isLegal(drawn, upCard, calledColor, handForLegal(hand))) {
                    if (!humanPlayers.get(currentPlayer).booleanValue()) {
                        chosen = hand.size() - 1;
                    } else {
                        System.out.print("Play drawn card " + drawn + "? y/n: ");
                        String answer = scanner.nextLine();
                        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                            chosen = hand.size() - 1;
                        }
                    }
                }
            }

            if (chosen >= 0) {
                if (playCardAtIndex(chosen)) {
                    return;
                }
            } else {
                next();
            }
        }
        log.warn("turn limit reached");
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    boolean tryJumpIn() {
        if (!options.jumpIn || pendingDraws > 0 || upCard.startsWith("W")) {
            return false;
        }
        int n = playerNames.size();
        for (int step = 1; step < n; step++) {
            int p = normalizePlayer(currentPlayer + step * direction);
            ArrayList<String> hand = hands.get(p);
            int idx = bot.findJumpInIndex(hand, upCard);
            if (idx < 0) {
                continue;
            }
            if (humanPlayers.get(p).booleanValue() && !console.askJumpIn(scanner, upCard)) {
                continue;
            }
            currentPlayer = p;
            if (!quiet) {
                System.out.println(playerNames.get(p) + " jumps in with " + upCard);
            }
            playCardAtIndex(idx);
            return true;
        }
        return false;
    }

    boolean playCardAtIndex(int chosen) {
        String name = playerNames.get(currentPlayer);
        ArrayList<String> hand = hands.get(currentPlayer);

        if (chosen >= hand.size()) {
            if (!quiet) {
                System.out.println(name + " selected an invalid index and draws a penalty card.");
            }
            hand.add(draw());
            next();
            return false;
        }

        String card = hand.get(chosen);
        ArrayList<String> handBefore = copyHand(hand);

        if (pendingDraws > 0) {
            if (!rules.canStackDraw(card, pendingDraws, pendingDrawRank)) {
                if (!quiet) {
                    System.out.println(name + " cannot stack that card and draws a penalty card.");
                }
                hand.add(draw());
                next();
                return false;
            }
        } else if (!rules.isLegal(card, upCard, calledColor, handForLegal(handBefore))) {
            log.debug("{} tried illegal {}", name, card);
            if (!quiet) {
                System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
            }
            hand.add(draw());
            next();
            return false;
        }

        if (card.equals("W4") && options.w4OnlyWithoutColor) {
            lastW4Player = currentPlayer;
            lastW4HadColorMatch = rules.hasMatchingColor(handBefore, upCard, calledColor);
        }

        hand.remove(chosen);
        discard.add(upCard);
        upCard = card;
        calledColor = "";
        if (!quiet) {
            System.out.println(name + " plays " + card);
        }

        if (card.equals("W") || card.equals("W4")) {
            if (humanPlayers.get(currentPlayer).booleanValue()) {
                calledColor = console.askColor(scanner);
            } else {
                calledColor = bot.chooseColor(hand);
            }
            if (!quiet) {
                System.out.println(name + " calls " + calledColor);
            }
        }

        if (hand.size() == 1) {
            handleUnoCall(name);
        }

        if (hand.size() == 0) {
            finishRound(name);
            return true;
        }

        if (options.sevenZeroRule) {
            if (rules.isSeven(card)) {
                applySevenSwap();
            } else if (rules.isZero(card)) {
                rotateHands();
            }
        }

        applyCardEffect(card, handBefore);
        return false;
    }

    void applySevenSwap() {
        int target;
        if (humanPlayers.get(currentPlayer).booleanValue()) {
            target = console.askSwapTarget(scanner, playerNames, currentPlayer);
        } else {
            target = bot.chooseSwapTarget(hands, currentPlayer);
        }
        swapHands(currentPlayer, target);
        if (!quiet) {
            System.out.println(playerNames.get(currentPlayer) + " swaps hands with "
                    + playerNames.get(target));
        }
    }

    void swapHands(int a, int b) {
        ArrayList<String> temp = hands.get(a);
        hands.set(a, hands.get(b));
        hands.set(b, temp);
    }

    void rotateHands() {
        int n = playerNames.size();
        ArrayList<ArrayList<String>> copies = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < n; i++) {
            copies.add(copyHand(hands.get(i)));
        }
        for (int i = 0; i < n; i++) {
            int from = normalizePlayer(i - direction);
            hands.set(i, copies.get(from));
        }
        if (!quiet) {
            System.out.println("Hands rotated " + directionLabel());
        }
    }

    int normalizePlayer(int index) {
        int n = playerNames.size();
        while (index >= n) {
            index -= n;
        }
        while (index < 0) {
            index += n;
        }
        return index;
    }

    void handleUnoCall(String name) {
        if (!options.unoPenalty) {
            if (!quiet) {
                System.out.println(name + " says UNO!");
            }
            return;
        }
        boolean saidUno = true;
        if (humanPlayers.get(currentPlayer).booleanValue()) {
            saidUno = console.askUno(scanner);
        }
        if (saidUno) {
            if (!quiet) {
                System.out.println(name + " says UNO!");
            }
        } else {
            unoPenalty.set(currentPlayer, Boolean.TRUE);
            if (!quiet) {
                System.out.println(name + " forgot UNO.");
            }
        }
    }

    void finishRound(String name) {
        int points = 0;
        for (int i = 0; i < hands.size(); i++) {
            if (i != currentPlayer) {
                for (int j = 0; j < hands.get(i).size(); j++) {
                    points += rules.points(hands.get(i).get(j));
                }
            }
        }
        scores[currentPlayer] += points;
        hasWinner = true;
        winnerName = name;
        winnerPoints = points;
        log.info("{} wins, +{}", name, points);
        if (!quiet) {
            System.out.println(name + " wins and scores " + points);
        }
    }

    void applyOpeningCardEffect(String card) {
        CardRank rank = rules.rankOf(card);
        if (rank == CardRank.SKIP) {
            if (!quiet) {
                System.out.println("Opening skip skips " + playerNames.get(currentPlayer));
            }
            next();
        } else if (rank == CardRank.REVERSE) {
            direction = direction * -1;
            if (!quiet) {
                System.out.println("Opening reverse, direction " + directionLabel());
            }
            if (playerNames.size() == 2) {
                next();
            }
        } else if (rank == CardRank.DRAW_TWO) {
            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws two on opening card.");
            }
            drawCards(hands.get(currentPlayer), 2);
            next();
        }
    }

    void applyCardEffect(String card, ArrayList<String> handBefore) {
        CardRank rank = rules.rankOf(card);
        if (rank == CardRank.SKIP) {
            next();
            next();
        } else if (rank == CardRank.REVERSE) {
            direction = direction * -1;
            if (!quiet) {
                System.out.println("Direction " + directionLabel());
            }
            if (playerNames.size() == 2) {
                next();
                next();
            } else {
                next();
            }
        } else if (rank == CardRank.DRAW_TWO) {
            if (options.stackDraws && pendingDraws > 0) {
                pendingDraws += 2;
                if (!quiet) {
                    System.out.println("Draw stack now " + pendingDraws);
                }
                next();
            } else {
                pendingDraws = 2;
                pendingDrawRank = CardRank.DRAW_TWO;
                next();
                if (!resolveDrawPenalty(false)) {
                    next();
                }
            }
        } else if (rank == CardRank.WILD_DRAW_FOUR) {
            if (options.stackDraws && pendingDraws > 0) {
                pendingDraws += 4;
                pendingDrawRank = CardRank.WILD_DRAW_FOUR;
                if (!quiet) {
                    System.out.println("Draw stack now " + pendingDraws);
                }
                next();
            } else {
                pendingDraws = 4;
                pendingDrawRank = CardRank.WILD_DRAW_FOUR;
                next();
                if (!resolveDrawPenalty(true)) {
                    next();
                }
            }
        } else {
            pendingDraws = 0;
            pendingDrawRank = null;
            next();
        }
    }

    boolean resolveDrawPenalty(boolean wildDrawFour) {
        String victim = playerNames.get(currentPlayer);
        ArrayList<String> hand = hands.get(currentPlayer);
        boolean challenged = false;

        if (wildDrawFour && options.w4Challenge) {
            if (humanPlayers.get(currentPlayer).booleanValue()) {
                challenged = console.askChallenge(scanner);
            } else {
                challenged = bot.willChallenge();
            }
        }

        if (challenged) {
            if (lastW4HadColorMatch) {
                ArrayList<String> w4Hand = hands.get(lastW4Player);
                drawCards(w4Hand, 4);
                if (!quiet) {
                    System.out.println(playerNames.get(lastW4Player) + " draws 4, challenge succeeded.");
                }
                pendingDraws = 0;
                pendingDrawRank = null;
                return true;
            } else {
                drawCards(hand, 6);
                if (!quiet) {
                    System.out.println(victim + " draws 6, challenge failed.");
                }
                pendingDraws = 0;
                pendingDrawRank = null;
                return true;
            }
        }

        drawCards(hand, pendingDraws);
        if (!quiet) {
            System.out.println(victim + " draws " + pendingDraws + ".");
        }
        pendingDraws = 0;
        pendingDrawRank = null;
        return false;
    }

    void drawCards(ArrayList<String> hand, int count) {
        for (int i = 0; i < count; i++) {
            hand.add(draw());
        }
    }

    String draw() {
        return deck.draw(deckCards, discard, random);
    }

    void next() {
        currentPlayer += direction;
        if (currentPlayer >= playerNames.size()) {
            currentPlayer = 0;
        }
        if (currentPlayer < 0) {
            currentPlayer = playerNames.size() - 1;
        }
    }

    ArrayList<String> handForLegal(ArrayList<String> hand) {
        if (options.w4OnlyWithoutColor) {
            return hand;
        }
        return null;
    }

    String directionLabel() {
        return direction > 0 ? "clockwise" : "counter-clockwise";
    }

    String colorSuffix() {
        return calledColor.equals("") ? "" : " called " + calledColor;
    }

    static ArrayList<String> copyHand(ArrayList<String> hand) {
        ArrayList<String> copy = new ArrayList<String>();
        for (int i = 0; i < hand.size(); i++) {
            copy.add(hand.get(i));
        }
        return copy;
    }

    static String join(ArrayList<String> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) {
                out += " ";
            }
        }
        return out;
    }

    public boolean hasWinner() {
        return hasWinner;
    }

    public String winnerName() {
        return winnerName;
    }

    public int winnerPoints() {
        return winnerPoints;
    }
}
