import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "round_scores")
public class RoundScoreRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private RoundRecord round;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private PlayerRecord player;

    @Column(nullable = false)
    private int score;

    public RoundScoreRecord() {
    }

    public Long getId() {
        return id;
    }

    public RoundRecord getRound() {
        return round;
    }

    public void setRound(RoundRecord round) {
        this.round = round;
    }

    public PlayerRecord getPlayer() {
        return player;
    }

    public void setPlayer(PlayerRecord player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
