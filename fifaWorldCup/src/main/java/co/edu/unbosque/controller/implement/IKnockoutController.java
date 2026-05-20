package co.edu.unbosque.controller.implement;

import co.edu.unbosque.model.dto.KnockoutMatchDTO;
import org.springframework.http.ResponseEntity;

public interface IKnockoutController {
	ResponseEntity<?> closeGroupStage();

	ResponseEntity<?> getBracket();

	ResponseEntity<?> updateKnockoutMatch(String id, KnockoutMatchDTO dto);
}