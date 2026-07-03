package persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class GameRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long seed;

    @Column(nullable = false)
    private Instant startedAt;

    @Column(nullable = false)
    private int playerCount;

    @Column(nullable = false)
    private boolean humanIncluded;

    @OneToMany(mappedBy = "game")
    private List<RoundRecord> rounds = new ArrayList<>();

    public GameRecord() {
    }

    public Long getId() {
        return id;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public boolean isHumanIncluded() {
        return humanIncluded;
    }

    public void setHumanIncluded(boolean humanIncluded) {
        this.humanIncluded = humanIncluded;
    }

    public List<RoundRecord> getRounds() {
        return rounds;
    }
}
