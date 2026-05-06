package co.edu.unbosque.model.dto;

public class TeamDTO {

	private String id;
	private String name;
	private String confederation;
	private String groupId;

	public TeamDTO() {
	}

	public TeamDTO(String id, String name, String confederation, String groupId) {
		this.id = id;
		this.name = name;
		this.confederation = confederation;
		this.groupId = groupId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfederation() {
		return confederation;
	}

	public void setConfederation(String confederation) {
		this.confederation = confederation;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}