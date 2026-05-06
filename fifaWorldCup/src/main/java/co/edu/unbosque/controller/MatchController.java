package co.edu.unbosque.controller;

import co.edu.unbosque.exception.BadRequestException;
import co.edu.unbosque.exception.ResourceNotFoundException;
import co.edu.unbosque.model.Match;
import co.edu.unbosque.model.dto.MatchDTO;
import co.edu.unbosque.persistence.GroupRepository;
import co.edu.unbosque.persistence.MatchRepository;
import co.edu.unbosque.persistence.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchController implements IMatchController {

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private GroupRepository groupRepository;

	private void validate(MatchDTO dto) {
		if (dto == null || dto.getId() == null || dto.getId().isBlank() || dto.getHomeTeamId() == null
				|| dto.getHomeTeamId().isBlank() || dto.getAwayTeamId() == null || dto.getAwayTeamId().isBlank()
				|| dto.getGroupId() == null || dto.getGroupId().isBlank()) {
			throw new BadRequestException("Todos los campos del partido son obligatorios");
		}
		if (!groupRepository.existsById(dto.getGroupId()))
			throw new BadRequestException("El grupo " + dto.getGroupId() + " no existe");
		if (!teamRepository.existsById(dto.getHomeTeamId()))
			throw new BadRequestException("El equipo local " + dto.getHomeTeamId() + " no existe");
		if (!teamRepository.existsById(dto.getAwayTeamId()))
			throw new BadRequestException("El equipo visitante " + dto.getAwayTeamId() + " no existe");
	}

	private MatchDTO mapToDto(Match m) {
		MatchDTO dto = new MatchDTO(m.getId(), m.getGroupId(), m.getHomeTeamId(), m.getAwayTeamId(), m.getHomeGoals(),
				m.getAwayGoals(), m.isPlayed());
		dto.setHomeYellowCards(m.getHomeYellowCards());
		dto.setAwayYellowCards(m.getAwayYellowCards());
		dto.setHomeRedCards(m.getHomeRedCards());
		dto.setAwayRedCards(m.getAwayRedCards());
		return dto;
	}

	private Match mapToEntity(MatchDTO dto) {
		Match m = new Match(dto.getId(), dto.getGroupId(), dto.getHomeTeamId(), dto.getAwayTeamId());
		m.setHomeGoals(dto.getHomeGoals());
		m.setAwayGoals(dto.getAwayGoals());
		m.setPlayed(dto.isPlayed());
		m.setHomeYellowCards(dto.getHomeYellowCards());
		m.setAwayYellowCards(dto.getAwayYellowCards());
		m.setHomeRedCards(dto.getHomeRedCards());
		m.setAwayRedCards(dto.getAwayRedCards());
		return m;
	}

	@Override
	@PostMapping
	public ResponseEntity<?> addMatch(@RequestBody MatchDTO dto) {
		validate(dto);
		Match saved = matchRepository.save(mapToEntity(dto));
		return ResponseEntity.status(201).body(mapToDto(saved));
	}

	@Override
	@PutMapping("/{id}")
	public ResponseEntity<?> updateMatch(@PathVariable String id, @RequestBody MatchDTO dto) {
		if (!matchRepository.existsById(id))
			throw new ResourceNotFoundException("Partido con id " + id + " no encontrado");
		dto.setId(id);
		Match updated = matchRepository.save(mapToEntity(dto));
		return ResponseEntity.ok(mapToDto(updated));
	}

	@Override
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteMatch(@PathVariable String id) {
		if (!matchRepository.existsById(id))
			throw new ResourceNotFoundException("Partido con id " + id + " no encontrado");
		matchRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	@GetMapping("/teams/{teamId}")
	public ResponseEntity<?> getMatchesByTeam(@PathVariable String teamId) {
		if (!teamRepository.existsById(teamId))
			throw new ResourceNotFoundException("Equipo con id " + teamId + " no encontrado");
		List<MatchDTO> result = matchRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId).stream()
				.map(this::mapToDto).toList();
		return ResponseEntity.ok(result);
	}

	@Override
	@GetMapping("/groups/{groupId}")
	public ResponseEntity<?> getMatchesByGroup(@PathVariable String groupId) {
		if (!groupRepository.existsById(groupId))
			throw new ResourceNotFoundException("Grupo " + groupId + " no encontrado");
		List<MatchDTO> result = matchRepository.findByGroupId(groupId).stream().map(this::mapToDto).toList();
		return ResponseEntity.ok(result);
	}
}