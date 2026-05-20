package co.edu.unbosque.controller;

import co.edu.unbosque.controller.implement.IGroupController;
import co.edu.unbosque.exception.BadRequestException;
import co.edu.unbosque.exception.ResourceNotFoundException;
import co.edu.unbosque.model.Group;
import co.edu.unbosque.model.Match;
import co.edu.unbosque.model.Team;
import co.edu.unbosque.model.dto.GroupDTO;
import co.edu.unbosque.model.dto.StandingDTO;
import co.edu.unbosque.persistence.GroupRepository;
import co.edu.unbosque.persistence.MatchRepository;
import co.edu.unbosque.persistence.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController implements IGroupController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private MatchRepository matchRepository;

	private GroupDTO mapToDto(Group g) {
		return new GroupDTO(g.getId(), g.getMatchesPlayed());
	}

	private List<StandingDTO> calculateStandings(String groupId) {
		List<Team> teams = teamRepository.findByGroupId(groupId);
		List<Match> matches = matchRepository.findByGroupId(groupId);

		Map<String, StandingDTO> standingsMap = new HashMap<>();
		for (Team t : teams)
			standingsMap.put(t.getId(), new StandingDTO(t.getId(), t.getName()));

		for (Match m : matches) {
			if (!m.isPlayed()) continue;
			StandingDTO home = standingsMap.get(m.getHomeTeamId());
			StandingDTO away = standingsMap.get(m.getAwayTeamId());
			if (home != null)
				home.addMatch(m.getHomeGoals(), m.getAwayGoals(),
						m.getHomeYellowCards(), m.getHomeRedCards());
			if (away != null)
				away.addMatch(m.getAwayGoals(), m.getHomeGoals(),
						m.getAwayYellowCards(), m.getAwayRedCards());
		}

		List<StandingDTO> standings = new ArrayList<>(standingsMap.values());
		standings.sort(Comparator.comparingInt(StandingDTO::getPoints).reversed()
				.thenComparingInt(StandingDTO::getGoalDifference).reversed()
				.thenComparingInt(StandingDTO::getGoalsFor).reversed()
				.thenComparingInt(StandingDTO::getYellowCards)
				.thenComparingInt(StandingDTO::getRedCards));
		return standings;
	}

	@Override
	@GetMapping
	public ResponseEntity<?> getAllGroups() {
		List<GroupDTO> result = groupRepository.findAll().stream()
				.map(this::mapToDto).toList();
		return ResponseEntity.ok(result);
	}

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<?> getGroupById(@PathVariable String id) {
		Group group = groupRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Grupo " + id + " no encontrado"));
		return ResponseEntity.ok(mapToDto(group));
	}

	@Override
	@GetMapping("/{id}/standings")
	public ResponseEntity<?> getGroupStandings(@PathVariable String id) {
		if (!groupRepository.existsById(id))
			throw new ResourceNotFoundException("Grupo " + id + " no encontrado");
		return ResponseEntity.ok(calculateStandings(id));
	}

	@Override
	@PostMapping("/{id}/generate-matches")
	public ResponseEntity<?> generateMatches(@PathVariable String id) {
		
		if (!groupRepository.existsById(id))
			throw new ResourceNotFoundException("Grupo " + id + " no encontrado");

		
		List<Team> teams = teamRepository.findByGroupId(id);
		if (teams.size() < 4) {
			throw new BadRequestException(
					"El grupo " + id + " necesita 4 equipos para generar partidos. "
					+ "Actualmente tiene " + teams.size());
		}

		
		List<Match> existing = matchRepository.findByGroupId(id);
		if (!existing.isEmpty()) {
			throw new BadRequestException(
					"El grupo " + id + " ya tiene partidos generados");
		}

		
		List<Match> matches = new ArrayList<>();
		int matchNumber = 1;

		for (int i = 0; i < teams.size(); i++) {
			for (int j = i + 1; j < teams.size(); j++) {
				String matchId = "G" + id + "-M" + matchNumber;
				matches.add(new Match(matchId, id,
						teams.get(i).getId(),
						teams.get(j).getId()));
				matchNumber++;
			}
		}

		matchRepository.saveAll(matches);
		return ResponseEntity.status(201)
				.body("Se generaron " + matches.size()
						+ " partidos para el grupo " + id);
	}
}