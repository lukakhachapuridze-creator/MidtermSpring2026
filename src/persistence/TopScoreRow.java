package persistence;

public class TopScoreRow {
    private String name;
    private int totalScore;

    public TopScoreRow(String name, int totalScore) {
        this.name = name;
        this.totalScore = totalScore;
    }

    public String getName() {
        return name;
    }

    public int getTotalScore() {
        return totalScore;
    }
}
