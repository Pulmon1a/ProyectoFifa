package co.edu.unbosque.controller;

import co.edu.unbosque.exception.BadRequestException;
import co.edu.unbosque.exception.ResourceNotFoundException;
import co.edu.unbosque.model.Team;
import co.edu.unbosque.model.dto.TeamDTO;
import co.edu.unbosque.persistence.GroupRepository;
import co.edu.unbosque.persistence.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController implements ITeamController {

	private static final int MAX_TEAMS_PER_GROUP = 4;
	private static final int MAX_UEFA_PER_GROUP = 2;
	private static final int MAX_TOTAL_TEAMS = 48;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private GroupRepository groupRepository;

	private void validate(TeamDTO dto) {
		if (dto == null || dto.getId() == null || dto.getId().isBlank() || dto.getName() == null
				|| dto.getName().isBlank() || dto.getConfederation() == null || dto.getConfederation().isBlank()
				|| dto.getGroupId() == null || dto.getGroupId().isBlank()) {
			throw new BadRequestException("Todos los campos del equipo son obligatorios");
		}

		if (!groupRepository.existsById(dto.getGroupId())) {
			throw new BadRequestException("El grupo " + dto.getGroupId() + " no existe");
		}

		if (teamRepository.count() >= MAX_TOTAL_TEAMS) {
			throw new BadRequestException("Ya se alcanzó el máximo de " + MAX_TOTAL_TEAMS + " equipos en el torneo");
		}

		List<Team> teamsInGroup = teamRepository.findByGroupId(dto.getGroupId());

		if (teamsInGroup.size() >= MAX_TEAMS_PER_GROUP) {
			throw new BadRequestException(
					"El grupo " + dto.getGroupId() + " ya tiene el máximo de " + MAX_TEAMS_PER_GROUP + " equipos");
		}

		long sameConfederation = teamsInGroup.stream()
				.filter(t -> t.getConfederation().equalsIgnoreCase(dto.getConfederation())).count();

		if (dto.getConfederation().equalsIgnoreCase("UEFA")) {
			if (sameConfederation >= MAX_UEFA_PER_GROUP) {
				throw new BadRequestException("El grupo " + dto.getGroupId() + " ya tiene el máximo de "
						+ MAX_UEFA_PER_GROUP + " equipos UEFA");
			}
		} else {
			if (sameConfederation >= 1) {
				throw new BadRequestException("El grupo " + dto.getGroupId()
						+ " ya tiene un equipo de la confederación " + dto.getConfederation());
			}
		}
	}

	private TeamDTO mapToDto(Team t) {
		return new TeamDTO(t.getId(), t.getName(), t.getConfederation(), t.getGroupId());
	}

	private Team mapToEntity(TeamDTO dto) {
		return new Team(dto.getId(), dto.getName(), dto.getConfederation(), dto.getGroupId());
	}

	@Override
	@PostMapping
	public ResponseEntity<?> addTeam(@RequestBody TeamDTO dto) {
		validate(dto);
		Team saved = teamRepository.save(mapToEntity(dto));
		return ResponseEntity.status(201).body(mapToDto(saved));
	}

	@Override
	@PutMapping("/{id}")
	public ResponseEntity<?> updateTeam(@PathVariable String id, @RequestBody TeamDTO dto) {
		if (!teamRepository.existsById(id))
			throw new ResourceNotFoundException("Equipo con id " + id + " no encontrado");
		dto.setId(id);
		Team updated = teamRepository.save(mapToEntity(dto));
		return ResponseEntity.ok(mapToDto(updated));
	}

	@Override
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTeam(@PathVariable String id) {
		if (!teamRepository.existsById(id))
			throw new ResourceNotFoundException("Equipo con id " + id + " no encontrado");
		teamRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	@GetMapping
	public ResponseEntity<?> getAllTeams() {
		List<TeamDTO> result = teamRepository.findAll().stream().map(this::mapToDto).toList();
		return ResponseEntity.ok(result);
	}

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<?> getTeamById(@PathVariable String id) {
		Team team = teamRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Equipo con id " + id + " no encontrado"));
		return ResponseEntity.ok(mapToDto(team));
	}
}