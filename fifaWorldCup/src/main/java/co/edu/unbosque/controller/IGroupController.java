package co.edu.unbosque.controller;

import org.springframework.http.ResponseEntity;

public interface IGroupController {
	ResponseEntity<?> getAllGroups();

	ResponseEntity<?> getGroupById(String id);

	ResponseEntity<?> getGroupStandings(String id);

	ResponseEntity<?> generateMatches(String id);
}