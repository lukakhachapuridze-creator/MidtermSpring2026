import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rounds")
public class RoundRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id")
    private GameRecord game;

    @Column(nullable = false)
    private int roundNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "winner_id")
    private PlayerRecord winner;

    @Column(nullable = false)
    private int winnerPoints;

    @Column(nullable = false)
    private Instant completedAt;

    @OneToMany(mappedBy = "round")
    private List<RoundScoreRecord> scores = new ArrayList<>();

    public RoundRecord() {
    }

    public Long getId() {
        return id;
    }

    public GameRecord getGame() {
        return game;
    }

    public void setGame(GameRecord game) {
        this.game = game;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public PlayerRecord getWinner() {
        return winner;
    }

    public void setWinner(PlayerRecord winner) {
        this.winner = winner;
    }

    public int getWinnerPoints() {
        return winnerPoints;
    }

    public void setWinnerPoints(int winnerPoints) {
        this.winnerPoints = winnerPoints;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public List<RoundScoreRecord> getScores() {
        return scores;
    }
}
