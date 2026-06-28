import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceTest {
    PersistenceService store;

    @BeforeEach
    void setUp() {
        PersistenceService.resetFactory();
        String url = "jdbc:h2:mem:test" + System.nanoTime() + ";DB_CLOSE_DELAY=-1";
        store = new PersistenceService(url);
    }

    @AfterEach
    void tearDown() {
        store.close();
        PersistenceService.resetFactory();
    }

    @Test
    void savesAllPlayersAndRoundScores() {
        long gameId = store.startGame(99, Arrays.asList("Bot1", "Bot2", "Bot3"),
                Arrays.asList(false, false, false));
        store.saveRound(gameId, 1, "Bot2", 45,
                Arrays.asList("Bot1", "Bot2", "Bot3"), Arrays.asList(false, false, false));

        RoundRecord round = store.recentRounds(1).get(0);
        assertEquals(1, round.getRoundNumber());
        assertEquals("Bot2", round.getWinner().getName());
        assertEquals(45, round.getWinnerPoints());
        assertEquals(99, round.getGame().getSeed());

        var scores = store.scoresForRound(round.getId());
        assertEquals(3, scores.size());
        assertEquals(0, scoreFor(scores, "Bot1"));
        assertEquals(45, scoreFor(scores, "Bot2"));
        assertEquals(0, scoreFor(scores, "Bot3"));
    }

    @Test
    void updatesWinCountsAndTopScores() {
        long gameId = store.startGame(1, Arrays.asList("Bot1", "Bot2"),
                Arrays.asList(false, false));
        store.saveRound(gameId, 1, "Bot1", 30,
                Arrays.asList("Bot1", "Bot2"), Arrays.asList(false, false));
        store.saveRound(gameId, 2, "Bot1", 20,
                Arrays.asList("Bot1", "Bot2"), Arrays.asList(false, false));
        store.saveRound(gameId, 3, "Bot2", 15,
                Arrays.asList("Bot1", "Bot2"), Arrays.asList(false, false));

        WinCountRow bot1Wins = store.playerWinCounts().get(0);
        assertEquals("Bot1", bot1Wins.getName());
        assertEquals(2, bot1Wins.getWins());

        TopScoreRow top = store.highestScores(1).get(0);
        assertEquals("Bot1", top.getName());
        assertEquals(50, top.getTotalScore());
    }

    @Test
    void recentGamesListsSessionsWithRounds() {
        long gameId = store.startGame(42, Arrays.asList("Bot1", "Bot2"),
                Arrays.asList(false, false));
        store.saveRound(gameId, 1, "Bot1", 10,
                Arrays.asList("Bot1", "Bot2"), Arrays.asList(false, false));

        GameRecord game = store.recentGames(1).get(0);
        assertEquals(42, game.getSeed());
        assertEquals(2, game.getPlayerCount());
        assertEquals(1, game.getRounds().size());
    }

    @Test
    void persistsHumanPlayer() {
        long gameId = store.startGame(7, Arrays.asList("You", "Bot1"),
                Arrays.asList(true, false));
        store.saveRound(gameId, 1, "You", 25,
                Arrays.asList("You", "Bot1"), Arrays.asList(true, false));

        PlayerRecord you = store.standings().stream()
                .filter(p -> p.getName().equals("You"))
                .findFirst()
                .orElseThrow();
        assertTrue(you.isHuman());
        assertEquals(25, you.getTotalScore());
        assertEquals(1, you.getWins());
    }

    private static int scoreFor(java.util.List<RoundScoreRecord> scores, String name) {
        for (RoundScoreRecord row : scores) {
            if (row.getPlayer().getName().equals(name)) {
                return row.getScore();
            }
        }
        return -1;
    }
}
