package co.edu.unbosque.controller;

import co.edu.unbosque.controller.implement.IKnockoutController;
import co.edu.unbosque.exception.BadRequestException;
import co.edu.unbosque.exception.ResourceNotFoundException;
import co.edu.unbosque.model.KnockoutMatch;
import co.edu.unbosque.model.Match;
import co.edu.unbosque.model.Team;
import co.edu.unbosque.model.dto.KnockoutMatchDTO;
import co.edu.unbosque.model.dto.StandingDTO;
import co.edu.unbosque.persistence.KnockoutMatchRepository;
import co.edu.unbosque.persistence.MatchRepository;
import co.edu.unbosque.persistence.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/knockout")
public class KnockoutController implements IKnockoutController {

    @Autowired
    private KnockoutMatchRepository knockoutMatchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    private KnockoutMatchDTO mapToDto(KnockoutMatch m) {
        return new KnockoutMatchDTO(m.getId(), m.getRound(), m.getHomeTeamId(), m.getAwayTeamId(), m.getHomeGoals(),
                m.getAwayGoals(), m.getWinnerId(), m.isPlayed());
    }

    private List<StandingDTO> calculateStandings(String groupId) {
        List<Team> teams = teamRepository.findByGroupId(groupId);
        List<Match> matches = matchRepository.findByGroupId(groupId);

        Map<String, StandingDTO> map = new HashMap<>();
        for (Team t : teams)
            map.put(t.getId(), new StandingDTO(t.getId(), t.getName()));

        for (Match m : matches) {
            if (!m.isPlayed()) continue;
            StandingDTO home = map.get(m.getHomeTeamId());
            StandingDTO away = map.get(m.getAwayTeamId());
            if (home != null)
                home.addMatch(m.getHomeGoals(), m.getAwayGoals(), m.getHomeYellowCards(), m.getHomeRedCards());
            if (away != null)
                away.addMatch(m.getAwayGoals(), m.getHomeGoals(), m.getAwayYellowCards(), m.getAwayRedCards());
        }

        List<StandingDTO> standings = new ArrayList<>(map.values());
        standings.sort(Comparator.comparingInt(StandingDTO::getPoints).reversed()
                .thenComparingInt(StandingDTO::getGoalDifference).reversed()
                .thenComparingInt(StandingDTO::getGoalsFor).reversed()
                .thenComparingInt(StandingDTO::getYellowCards)
                .thenComparingInt(StandingDTO::getRedCards));
        return standings;
    }

    private void validateAllMatchesPlayed() {
        String[] groups = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };
        for (String groupId : groups) {
            List<Match> matches = matchRepository.findByGroupId(groupId);
            if (matches.size() < 6)
                throw new BadRequestException("El grupo " + groupId + " no tiene los 6 partidos registrados. Tiene " + matches.size() + " partido(s)");
            long unplayed = matches.stream().filter(m -> !m.isPlayed()).count();
            if (unplayed > 0)
                throw new BadRequestException("El grupo " + groupId + " tiene " + unplayed + " partido(s) sin jugar.");
        }
    }

    private void advanceBracket(String currentRound, String nextRound) {
        List<KnockoutMatch> currentMatches = knockoutMatchRepository.findByRound(currentRound);
        boolean allPlayed = currentMatches.stream().allMatch(KnockoutMatch::isPlayed);
        if (!allPlayed) return;

        List<KnockoutMatch> nextMatches = knockoutMatchRepository.findByRound(nextRound);
        if (!nextMatches.isEmpty()) return;

        List<KnockoutMatch> sorted = currentMatches.stream()
                .sorted(Comparator.comparing(KnockoutMatch::getId)).toList();

        List<String> winners = sorted.stream().map(KnockoutMatch::getWinnerId).toList();
        List<KnockoutMatch> newMatches = new ArrayList<>();
        String prefix = nextRound.replace(" ", "").substring(0, 3).toUpperCase();

        for (int i = 0; i < winners.size() / 2; i++) {
            String matchId = prefix + "-" + (i + 1);
            newMatches.add(new KnockoutMatch(matchId, nextRound, winners.get(i), winners.get(winners.size() - 1 - i)));
        }
        knockoutMatchRepository.saveAll(newMatches);
    }

    private void generateThirdPlace(String currentRound) {
        List<KnockoutMatch> semiMatches = knockoutMatchRepository.findByRound(currentRound);
        
        if (semiMatches.isEmpty()) return;
        
        boolean allPlayed = semiMatches.stream().allMatch(KnockoutMatch::isPlayed);
        if (!allPlayed) return;

        List<KnockoutMatch> existing = knockoutMatchRepository.findByRound("Third Place");
        if (!existing.isEmpty()) return;

        List<KnockoutMatch> sorted = semiMatches.stream()
                .sorted(Comparator.comparing(KnockoutMatch::getId)).toList();

        if (sorted.size() < 2) return;

        List<String> losers = new ArrayList<>();
        for (KnockoutMatch m : sorted) {
            String loser = m.getWinnerId().equals(m.getHomeTeamId()) ? m.getAwayTeamId() : m.getHomeTeamId();
            losers.add(loser);
        }

        KnockoutMatch thirdPlace = new KnockoutMatch("THP-1", "Third Place", losers.get(0), losers.get(1));
        knockoutMatchRepository.save(thirdPlace);
    }

    private String getGroupWinner(String groupId) {
        List<StandingDTO> standings = calculateStandings(groupId);
        return standings.isEmpty() ? null : standings.get(0).getTeamId();
    }

    private String getGroupRunnerUp(String groupId) {
        List<StandingDTO> standings = calculateStandings(groupId);
        return standings.size() < 2 ? null : standings.get(1).getTeamId();
    }

    private List<String> getBestThirds() {
        String[] groupIds = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };
        List<StandingDTO> allThirds = new ArrayList<>();
        for (String groupId : groupIds) {
            List<StandingDTO> standings = calculateStandings(groupId);
            if (standings.size() >= 3)
                allThirds.add(standings.get(2));
        }
        allThirds.sort(Comparator.comparingInt(StandingDTO::getPoints).reversed()
                .thenComparingInt(StandingDTO::getGoalDifference).reversed()
                .thenComparingInt(StandingDTO::getGoalsFor).reversed()
                .thenComparingInt(StandingDTO::getYellowCards)
                .thenComparingInt(StandingDTO::getRedCards));
        List<String> best8 = new ArrayList<>();
        for (int i = 0; i < Math.min(8, allThirds.size()); i++)
            best8.add(allThirds.get(i).getTeamId());
        return best8;
    }

    @Override
    @PostMapping("/close-group-stage")
    public ResponseEntity<?> closeGroupStage() {
        if (!knockoutMatchRepository.findAll().isEmpty())
            throw new BadRequestException("La fase de eliminación ya fue generada");

        validateAllMatchesPlayed();

        String[] groups = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };
        List<String> winners = new ArrayList<>();
        List<String> runnersUp = new ArrayList<>();

        for (String g : groups) {
            String winner = getGroupWinner(g);
            String runnerUp = getGroupRunnerUp(g);
            if (winner != null) winners.add(winner);
            if (runnerUp != null) runnersUp.add(runnerUp);
        }

        List<String> bestThirds = getBestThirds();
        List<String> qualified = new ArrayList<>();
        qualified.addAll(winners);
        qualified.addAll(runnersUp);
        qualified.addAll(bestThirds);

        if (qualified.size() < 32)
            throw new BadRequestException("No hay suficientes equipos clasificados. Se necesitan 32, hay " + qualified.size());

        List<KnockoutMatch> matches = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            String matchId = "R32-" + (i + 1);
            matches.add(new KnockoutMatch(matchId, "Round of 32", qualified.get(i), qualified.get(31 - i)));
        }
        knockoutMatchRepository.saveAll(matches);
        return ResponseEntity.ok("Fase de eliminación directa generada con " + matches.size() + " partidos");
    }

    @Override
    @GetMapping("/bracket")
    public ResponseEntity<?> getBracket() {
        List<KnockoutMatch> all = knockoutMatchRepository.findAll();
        if (all.isEmpty())
            throw new BadRequestException("La fase de grupos aún no ha sido cerrada");

        Map<String, List<KnockoutMatchDTO>> bracket = new LinkedHashMap<>();
        bracket.put("Round of 32", knockoutMatchRepository.findByRound("Round of 32").stream().map(this::mapToDto).toList());
        bracket.put("Round of 16", knockoutMatchRepository.findByRound("Round of 16").stream().map(this::mapToDto).toList());
        bracket.put("Quarter Finals", knockoutMatchRepository.findByRound("Quarter Finals").stream().map(this::mapToDto).toList());
        bracket.put("Semi Finals", knockoutMatchRepository.findByRound("Semi Finals").stream().map(this::mapToDto).toList());
        bracket.put("Third Place", knockoutMatchRepository.findByRound("Third Place").stream().map(this::mapToDto).toList());
        bracket.put("Final", knockoutMatchRepository.findByRound("Final").stream().map(this::mapToDto).toList());

        return ResponseEntity.ok(bracket);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> updateKnockoutMatch(@PathVariable String id, @RequestBody KnockoutMatchDTO dto) {
        KnockoutMatch match = knockoutMatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido con id " + id + " no encontrado"));

        match.setHomeGoals(dto.getHomeGoals());
        match.setAwayGoals(dto.getAwayGoals());
        match.setPlayed(dto.isPlayed());

        if (dto.isPlayed()) {
            if (dto.getHomeGoals() > dto.getAwayGoals()) {
                match.setWinnerId(match.getHomeTeamId());
            } else if (dto.getAwayGoals() > dto.getHomeGoals()) {
                match.setWinnerId(match.getAwayTeamId());
            } else {
                throw new BadRequestException("En eliminación directa no puede haber empate, define un ganador");
            }
        }

        knockoutMatchRepository.save(match);

        if (dto.isPlayed()) {
            advanceBracket("Round of 32", "Round of 16");
            advanceBracket("Round of 16", "Quarter Finals");
            advanceBracket("Quarter Finals", "Semi Finals");
            generateThirdPlace("Semi Finals");
            advanceBracket("Semi Finals", "Final");
        }

        return ResponseEntity.ok(mapToDto(match));
    }
}