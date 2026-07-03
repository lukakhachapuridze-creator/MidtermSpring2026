package persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceService {
    private static final Logger log = LoggerFactory.getLogger(PersistenceService.class);
    private static EntityManagerFactory factory;
    private static String overrideUrl;

    public PersistenceService() {
    }

    public PersistenceService(String url) {
        overrideUrl = url;
    }

    public long startGame(long seed, List<String> participants, List<Boolean> humanFlags) {
        EntityManager em = emf().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            boolean humanIncluded = false;
            for (int i = 0; i < participants.size(); i++) {
                findOrCreatePlayer(em, participants.get(i), humanFlags.get(i).booleanValue());
                if (humanFlags.get(i).booleanValue()) {
                    humanIncluded = true;
                }
            }

            GameRecord game = new GameRecord();
            game.setSeed(seed);
            game.setStartedAt(Instant.now());
            game.setPlayerCount(participants.size());
            game.setHumanIncluded(humanIncluded);
            em.persist(game);
            tx.commit();
            log.info("started game {}, seed {}", game.getId(), seed);
            return game.getId();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void saveRound(long gameId, int roundNumber, String winnerName, int winnerPoints,
                          List<String> participants, List<Boolean> humanFlags) {
        EntityManager em = emf().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            GameRecord game = em.find(GameRecord.class, gameId);
            if (game == null) {
                throw new IllegalArgumentException("game not found: " + gameId);
            }

            PlayerRecord winner = findOrCreatePlayer(em, winnerName, isHuman(humanFlags, participants, winnerName));

            RoundRecord round = new RoundRecord();
            round.setGame(game);
            round.setRoundNumber(roundNumber);
            round.setWinner(winner);
            round.setWinnerPoints(winnerPoints);
            round.setCompletedAt(Instant.now());
            em.persist(round);

            for (int i = 0; i < participants.size(); i++) {
                String name = participants.get(i);
                PlayerRecord player = findOrCreatePlayer(em, name, humanFlags.get(i).booleanValue());
                int roundScore = name.equals(winnerName) ? winnerPoints : 0;

                RoundScoreRecord scoreRow = new RoundScoreRecord();
                scoreRow.setRound(round);
                scoreRow.setPlayer(player);
                scoreRow.setScore(roundScore);
                em.persist(scoreRow);

                if (roundScore > 0) {
                    player.setTotalScore(player.getTotalScore() + roundScore);
                    player.setWins(player.getWins() + 1);
                }
            }

            tx.commit();
            log.info("saved round {} for game {}, winner {}", roundNumber, gameId, winnerName);
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<GameRecord> recentGames(int limit) {
        EntityManager em = emf().createEntityManager();
        try {
            return em.createQuery(
                            "select g from GameRecord g left join fetch g.rounds order by g.startedAt desc",
                            GameRecord.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<RoundRecord> recentRounds(int limit) {
        EntityManager em = emf().createEntityManager();
        try {
            return em.createQuery(
                            "select r from RoundRecord r "
                                    + "join fetch r.game join fetch r.winner "
                                    + "order by r.completedAt desc",
                            RoundRecord.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<WinCountRow> playerWinCounts() {
        EntityManager em = emf().createEntityManager();
        try {
            return em.createQuery(
                            "select new WinCountRow(p.name, p.wins) from PlayerRecord p "
                                    + "where p.wins > 0 order by p.wins desc, p.name asc",
                            WinCountRow.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<TopScoreRow> highestScores(int limit) {
        EntityManager em = emf().createEntityManager();
        try {
            return em.createQuery(
                            "select new TopScoreRow(p.name, p.totalScore) from PlayerRecord p "
                                    + "where p.totalScore > 0 order by p.totalScore desc, p.name asc",
                            TopScoreRow.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<PlayerRecord> standings() {
        EntityManager em = emf().createEntityManager();
        try {
            return em.createQuery(
                            "select p from PlayerRecord p order by p.totalScore desc, p.name asc",
                            PlayerRecord.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<RoundScoreRecord> scoresForRound(long roundId) {
        EntityManager em = emf().createEntityManager();
        try {
            return em.createQuery(
                            "select s from RoundScoreRecord s where s.round.id = :id order by s.player.name",
                            RoundScoreRecord.class)
                    .setParameter("id", roundId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            factory = null;
        }
    }

    public static void resetFactory() {
        closeStatic();
        overrideUrl = null;
    }

    private static void closeStatic() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
        factory = null;
    }

    private PlayerRecord findOrCreatePlayer(EntityManager em, String name, boolean human) {
        PlayerRecord player = em.createQuery(
                        "select p from PlayerRecord p where p.name = :name", PlayerRecord.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (player == null) {
            player = new PlayerRecord(name, human);
            em.persist(player);
        }
        return player;
    }

    private static boolean isHuman(List<Boolean> humanFlags, List<String> participants, String name) {
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).equals(name)) {
                return humanFlags.get(i).booleanValue();
            }
        }
        return false;
    }

    private static EntityManagerFactory emf() {
        if (factory == null) {
            if (overrideUrl != null) {
                Map<String, String> props = new HashMap<>();
                props.put("jakarta.persistence.jdbc.url", overrideUrl);
                factory = Persistence.createEntityManagerFactory("uno", props);
            } else {
                factory = Persistence.createEntityManagerFactory("uno");
            }
        }
        return factory;
    }
}
