package co.edu.unbosque.controller.implement;

import co.edu.unbosque.model.dto.MatchDTO;
import org.springframework.http.ResponseEntity;

public interface IMatchController {
	ResponseEntity<?> addMatch(MatchDTO dto);

	ResponseEntity<?> updateMatch(String id, MatchDTO dto);

	ResponseEntity<?> deleteMatch(String id);

	ResponseEntity<?> getMatchesByTeam(String teamId);

	ResponseEntity<?> getMatchesByGroup(String groupId);
}