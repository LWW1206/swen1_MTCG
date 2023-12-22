package at.technikum.apps.mtcg.entity;

public class UserStats {
    private String username;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int elo;

    public UserStats(String username, int gamesPlayed, int gamesWon, int gamesLost, int elo) {
        this.username = username;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.elo = elo;
    }

    @Override
    public String toString() {
        return "Username: " + username +
                ", Games Played: " + gamesPlayed +
                ", Games Won: " + gamesWon +
                ", Games Lost: " + gamesLost +
                ", ELO: " + elo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }
}
