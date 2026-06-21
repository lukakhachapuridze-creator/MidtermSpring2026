import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceService {
    private static final Logger log = LoggerFactory.getLogger(PersistenceService.class);
    private static EntityManagerFactory factory;

    public void saveWin(String winner, int points, long seed, int gameNumber) {
        EntityManager em = emf().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            GameRecord game = new GameRecord();
            game.setWinnerName(winner);
            game.setPoints(points);
            game.setSeed(seed);
            game.setGameNumber(gameNumber);
            game.setPlayedAt(Instant.now());
            em.persist(game);

            PlayerRecord player = em.createQuery(
                            "select p from PlayerRecord p where p.name = :name", PlayerRecord.class)
                    .setParameter("name", winner)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (player == null) {
                player = new PlayerRecord(winner);
                em.persist(player);
            }
            player.setTotalScore(player.getTotalScore() + points);

            tx.commit();
            log.info("saved win for {}, +{}", winner, points);
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
                            "select g from GameRecord g order by g.playedAt desc", GameRecord.class)
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
                            "select p from PlayerRecord p order by p.totalScore desc", PlayerRecord.class)
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

    private static EntityManagerFactory emf() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory("uno");
        }
        return factory;
    }
}
