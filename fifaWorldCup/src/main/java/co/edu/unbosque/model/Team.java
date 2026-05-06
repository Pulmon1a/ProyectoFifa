package co.edu.unbosque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")
public class Team {

	@Id
	private String id;
	private String name;
	private String confederation;
	private String groupId;

	public Team() {
	}

	public Team(String id, String name, String confederation, String groupId) {
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