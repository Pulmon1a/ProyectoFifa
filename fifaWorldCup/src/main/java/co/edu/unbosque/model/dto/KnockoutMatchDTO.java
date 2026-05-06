package co.edu.unbosque.model.dto;

public class KnockoutMatchDTO {

    private String id;
    private String round;
    private String homeTeamId;
    private String awayTeamId;
    private int homeGoals;
    private int awayGoals;
    private String winnerId;
    private boolean played;

    public KnockoutMatchDTO() {}

    public KnockoutMatchDTO(String id, String round, String homeTeamId, String awayTeamId,
                             int homeGoals, int awayGoals, String winnerId, boolean played) {
        this.id = id;
        this.round = round;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.winnerId = winnerId;
        this.played = played;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }

    public String getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(String homeTeamId) { this.homeTeamId = homeTeamId; }

    public String getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(String awayTeamId) { this.awayTeamId = awayTeamId; }

    public int getHomeGoals() { return homeGoals; }
    public void setHomeGoals(int homeGoals) { this.homeGoals = homeGoals; }

    public int getAwayGoals() { return awayGoals; }
    public void setAwayGoals(int awayGoals) { this.awayGoals = awayGoals; }

    public String getWinnerId() { return winnerId; }
    public void setWinnerId(String winnerId) { this.winnerId = winnerId; }

    public boolean isPlayed() { return played; }
    public void setPlayed(boolean played) { this.played = played; }
}