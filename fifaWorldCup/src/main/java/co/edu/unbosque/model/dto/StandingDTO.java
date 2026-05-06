package co.edu.unbosque.model.dto;

public class StandingDTO {

	private String teamId;
	private String teamName;
	private int played;
	private int won;
	private int drawn;
	private int lost;
	private int goalsFor;
	private int goalsAgainst;
	private int goalDifference;
	private int points;
	private int yellowCards;
	private int redCards;

	public StandingDTO() {
	}

	public StandingDTO(String teamId, String teamName) {
		this.teamId = teamId;
		this.teamName = teamName;
		this.played = 0;
		this.won = 0;
		this.drawn = 0;
		this.lost = 0;
		this.goalsFor = 0;
		this.goalsAgainst = 0;
		this.goalDifference = 0;
		this.points = 0;
		this.yellowCards = 0;
		this.redCards = 0;
	}

	public void addMatch(int goalsScored, int goalsConceded, int yellowCards, int redCards) {
		this.played++;
		this.goalsFor += goalsScored;
		this.goalsAgainst += goalsConceded;
		this.goalDifference = this.goalsFor - this.goalsAgainst;
		this.yellowCards += yellowCards;
		this.redCards += redCards;

		if (goalsScored > goalsConceded) {
			this.won++;
			this.points += 3;
		} else if (goalsScored == goalsConceded) {
			this.drawn++;
			this.points += 1;
		} else {
			this.lost++;
		}
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public int getPlayed() {
		return played;
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	public int getWon() {
		return won;
	}

	public void setWon(int won) {
		this.won = won;
	}

	public int getDrawn() {
		return drawn;
	}

	public void setDrawn(int drawn) {
		this.drawn = drawn;
	}

	public int getLost() {
		return lost;
	}

	public void setLost(int lost) {
		this.lost = lost;
	}

	public int getGoalsFor() {
		return goalsFor;
	}

	public void setGoalsFor(int goalsFor) {
		this.goalsFor = goalsFor;
	}

	public int getGoalsAgainst() {
		return goalsAgainst;
	}

	public void setGoalsAgainst(int goalsAgainst) {
		this.goalsAgainst = goalsAgainst;
	}

	public int getGoalDifference() {
		return goalDifference;
	}

	public void setGoalDifference(int goalDifference) {
		this.goalDifference = goalDifference;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getYellowCards() {
		return yellowCards;
	}

	public void setYellowCards(int yellowCards) {
		this.yellowCards = yellowCards;
	}

	public int getRedCards() {
		return redCards;
	}

	public void setRedCards(int redCards) {
		this.redCards = redCards;
	}
}