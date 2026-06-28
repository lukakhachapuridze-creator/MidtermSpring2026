public class WinCountRow {
    private String name;
    private long wins;

    public WinCountRow(String name, long wins) {
        this.name = name;
        this.wins = wins;
    }

    public String getName() {
        return name;
    }

    public long getWins() {
        return wins;
    }
}
