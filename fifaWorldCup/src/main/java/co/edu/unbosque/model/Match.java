package co.edu.unbosque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "matches")
public class Match {

	@Id
	private String id;
	private String groupId;
	private String homeTeamId;
	private String awayTeamId;
	private int homeGoals;
	private int awayGoals;
	private boolean played;
	private int homeYellowCards;
	private int awayYellowCards;
	private int homeRedCards;
	private int awayRedCards;

	public Match() {
	}

	public Match(String id, String groupId, String homeTeamId, String awayTeamId) {
		this.id = id;
		this.groupId = groupId;
		this.homeTeamId = homeTeamId;
		this.awayTeamId = awayTeamId;
		this.played = false;
		this.homeGoals = 0;
		this.awayGoals = 0;
		this.homeYellowCards = 0;
		this.awayYellowCards = 0;
		this.homeRedCards = 0;
		this.awayRedCards = 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getHomeTeamId() {
		return homeTeamId;
	}

	public void setHomeTeamId(String homeTeamId) {
		this.homeTeamId = homeTeamId;
	}

	public String getAwayTeamId() {
		return awayTeamId;
	}

	public void setAwayTeamId(String awayTeamId) {
		this.awayTeamId = awayTeamId;
	}

	public int getHomeGoals() {
		return homeGoals;
	}

	public void setHomeGoals(int homeGoals) {
		this.homeGoals = homeGoals;
	}

	public int getAwayGoals() {
		return awayGoals;
	}

	public void setAwayGoals(int awayGoals) {
		this.awayGoals = awayGoals;
	}

	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}

	public int getHomeYellowCards() {
		return homeYellowCards;
	}

	public void setHomeYellowCards(int homeYellowCards) {
		this.homeYellowCards = homeYellowCards;
	}

	public int getAwayYellowCards() {
		return awayYellowCards;
	}

	public void setAwayYellowCards(int awayYellowCards) {
		this.awayYellowCards = awayYellowCards;
	}

	public int getHomeRedCards() {
		return homeRedCards;
	}

	public void setHomeRedCards(int homeRedCards) {
		this.homeRedCards = homeRedCards;
	}

	public int getAwayRedCards() {
		return awayRedCards;
	}

	public void setAwayRedCards(int awayRedCards) {
		this.awayRedCards = awayRedCards;
	}
}