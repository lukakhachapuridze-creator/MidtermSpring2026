public interface CardRules {
    boolean isLegal(String card, String up, String call);

    String color(String card);

    CardColor colorOf(String card);

    String rank(String card);

    CardRank rankOf(String card);

    int number(String card);

    int points(String card);
}
