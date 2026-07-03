package service;

import interfaces.BotPlayer;
import interfaces.CardRules;
import interfaces.ConsoleInput;
import interfaces.Deck;
import model.GameOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFlowTest {
    GameService game;
    ArrayList<String> playerNames;
    ArrayList<Boolean> humanPlayers;
    ArrayList<ArrayList<String>> hands;
    int[] scores;

    @BeforeEach
    void setUp() {
        CardRules rules = new CardRulesService();
        Deck deck = new DeckService();
        BotPlayer bot = new BotPlayerService(rules);
        ConsoleInput console = new ConsoleInputService(rules);
        playerNames = new ArrayList<>();
        humanPlayers = new ArrayList<>();
        hands = new ArrayList<>();
        scores = new int[4];

        playerNames.add("A");
        playerNames.add("B");
        playerNames.add("C");
        humanPlayers.add(Boolean.FALSE);
        humanPlayers.add(Boolean.FALSE);
        humanPlayers.add(Boolean.FALSE);

        hands.add(new ArrayList<>());
        hands.add(new ArrayList<>());
        hands.add(new ArrayList<>());

        GameOptions options = GameOptions.defaults();
        options.sevenZeroRule = true;

        game = new GameService(playerNames, humanPlayers, hands, scores,
                new Random(1), true, new Scanner(""), rules, deck, bot, console, options);
    }

    @Test
    void swapHandsExchangesCards() {
        hands.get(0).add("R1");
        hands.get(0).add("R2");
        hands.get(1).add("G9");
        game.swapHands(0, 1);
        assertEquals("G9", hands.get(0).get(0));
        assertEquals("R1", hands.get(1).get(0));
        assertEquals("R2", hands.get(1).get(1));
    }

    @Test
    void rotateHandsPassesClockwise() {
        game.direction = 1;
        hands.get(0).add("A");
        hands.get(1).add("B");
        hands.get(2).add("C");
        game.rotateHands();
        assertEquals("C", hands.get(0).get(0));
        assertEquals("A", hands.get(1).get(0));
        assertEquals("B", hands.get(2).get(0));
    }

    @Test
    void rotateHandsPassesCounterClockwise() {
        game.direction = -1;
        hands.get(0).add("A");
        hands.get(1).add("B");
        hands.get(2).add("C");
        game.rotateHands();
        assertEquals("B", hands.get(0).get(0));
        assertEquals("C", hands.get(1).get(0));
        assertEquals("A", hands.get(2).get(0));
    }

    @Test
    void normalizePlayerWraps() {
        assertEquals(0, game.normalizePlayer(3));
        assertEquals(2, game.normalizePlayer(-1));
    }
}
