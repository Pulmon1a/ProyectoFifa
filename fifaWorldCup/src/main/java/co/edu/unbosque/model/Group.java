package co.edu.unbosque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "groups_table")
public class Group {

	@Id
	private String id;
	private int matchesPlayed;

	public Group() {
	}

	public Group(String id) {
		this.id = id;
		this.matchesPlayed = 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMatchesPlayed() {
		return matchesPlayed;
	}

	public void setMatchesPlayed(int matchesPlayed) {
		this.matchesPlayed = matchesPlayed;
	}
}