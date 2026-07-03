package service;

import interfaces.BotPlayer;
import interfaces.CardRules;
import interfaces.ConsoleInput;
import interfaces.Deck;
import interfaces.Game;
import model.CardRank;
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

    ArrayList<String> deckCards;
    ArrayList<String> discard;
    int currentPlayer;
    int direction;
    String upCard;
    String calledColor;

    boolean hasWinner;
    String winnerName;
    int winnerPoints;

    public GameService(ArrayList<String> playerNames, ArrayList<Boolean> humanPlayers,
                       ArrayList<ArrayList<String>> hands, int[] scores,
                       Random random, boolean quiet, Scanner scanner,
                       CardRules rules, Deck deck, BotPlayer bot, ConsoleInput console) {
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
    }

    public void play() {
        hasWinner = false;
        winnerName = "";
        winnerPoints = 0;
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

        int guard = 0;
        while (guard < 3000) {
            guard++;
            String name = playerNames.get(currentPlayer);
            ArrayList<String> hand = hands.get(currentPlayer);

            if (!quiet) {
                System.out.println("\nUp card: " + upCard + (calledColor.equals("") ? "" : " called " + calledColor));
                System.out.println(name + " hand: " + join(hand));
            }

            int chosen = -1;
            if (humanPlayers.get(currentPlayer).booleanValue()) {
                chosen = console.askCard(scanner, hand, upCard, calledColor);
            } else {
                chosen = bot.chooseCard(hand, upCard, calledColor);
            }

            if (chosen == -1) {
                String drawn = draw();
                hand.add(drawn);
                if (!quiet) {
                    System.out.println(name + " draws " + drawn);
                }
                if (rules.isLegal(drawn, upCard, calledColor)) {
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
                if (chosen >= hand.size()) {
                    if (!quiet) {
                        System.out.println(name + " selected an invalid index and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                String card = hand.get(chosen);

                if (!rules.isLegal(card, upCard, calledColor)) {
                    log.debug("{} tried illegal {}", name, card);
                    if (!quiet) {
                        System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
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

                if (hand.size() == 1 && !quiet) {
                    System.out.println(name + " says UNO!");
                }

                if (hand.size() == 0) {
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
                    return;
                }

                applyCardEffect(card);
            } else {
                next();
            }
        }
        log.warn("turn limit reached");
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
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

    void applyCardEffect(String card) {
        CardRank rank = rules.rankOf(card);
        if (rank == CardRank.SKIP) {
            next();
            next();
        } else if (rank == CardRank.REVERSE) {
            direction = direction * -1;
            if (playerNames.size() == 2) {
                next();
                next();
            } else {
                next();
            }
        } else if (rank == CardRank.DRAW_TWO) {
            next();
            hands.get(currentPlayer).add(draw());
            hands.get(currentPlayer).add(draw());
            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws two.");
            }
            next();
        } else if (rank == CardRank.WILD_DRAW_FOUR) {
            next();
            for (int i = 0; i < 4; i++) {
                hands.get(currentPlayer).add(draw());
            }
            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws four.");
            }
            next();
        } else {
            next();
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
}
