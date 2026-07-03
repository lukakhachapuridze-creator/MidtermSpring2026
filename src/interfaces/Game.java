package interfaces;

public interface Game {
    void play();

    boolean hasWinner();

    String winnerName();

    int winnerPoints();
}
