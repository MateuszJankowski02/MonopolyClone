package Utilities;

public class Player {
    private static int idCounter = 0;
    private int id;
    private String name;
    private int score;

    public Player(String name) {
        this.id = idCounter++;
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }
}
