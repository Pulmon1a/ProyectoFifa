package co.edu.unbosque.model.dto;

public class GroupDTO {

	private String id;
	private int matchesPlayed;

	public GroupDTO() {
	}

	public GroupDTO(String id, int matchesPlayed) {
		this.id = id;
		this.matchesPlayed = matchesPlayed;
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