package co.edu.unbosque.controller.implement;

import co.edu.unbosque.model.dto.TeamDTO;
import org.springframework.http.ResponseEntity;

public interface ITeamController {
	ResponseEntity<?> addTeam(TeamDTO dto);

	ResponseEntity<?> updateTeam(String id, TeamDTO dto);

	ResponseEntity<?> deleteTeam(String id);

	ResponseEntity<?> getAllTeams();

	ResponseEntity<?> getTeamById(String id);
}