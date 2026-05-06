package co.edu.unbosque.test;

import co.edu.unbosque.controller.KnockoutController;
import co.edu.unbosque.exception.BadRequestException;
import co.edu.unbosque.exception.ResourceNotFoundException;
import co.edu.unbosque.model.KnockoutMatch;
import co.edu.unbosque.model.Match;
import co.edu.unbosque.model.Team;
import co.edu.unbosque.model.dto.KnockoutMatchDTO;
import co.edu.unbosque.persistence.GroupRepository;
import co.edu.unbosque.persistence.KnockoutMatchRepository;
import co.edu.unbosque.persistence.MatchRepository;
import co.edu.unbosque.persistence.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnockoutControllerTest {

    @Mock
    private KnockoutMatchRepository knockoutMatchRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private KnockoutController knockoutController;

    // ── HELPERS ───────────────────────────────────────────

    // Crea un equipo simple
    private Team team(String id, String confederation, String groupId) {
        return new Team(id, id + "_name", confederation, groupId);
    }

    // Crea un partido ya jugado con resultado
    private Match playedMatch(String id, String groupId,
                               String home, String away,
                               int hg, int ag) {
        Match m = new Match(id, groupId, home, away);
        m.setHomeGoals(hg);
        m.setAwayGoals(ag);
        m.setPlayed(true);
        return m;
    }

    private void mockGroup(String groupId,
                            String t1, String t2, String t3, String t4) {
        when(teamRepository.findByGroupId(groupId)).thenReturn(List.of(
                team(t1, "CONMEBOL", groupId),
                team(t2, "UEFA",     groupId),
                team(t3, "CAF",      groupId),
                team(t4, "AFC",      groupId)
        ));
        when(matchRepository.findByGroupId(groupId)).thenReturn(List.of(
                playedMatch("m1_" + groupId, groupId, t1, t2, 3, 0), // t1 gana
                playedMatch("m2_" + groupId, groupId, t1, t3, 2, 0), // t1 gana
                playedMatch("m3_" + groupId, groupId, t1, t4, 1, 0), // t1 gana
                playedMatch("m4_" + groupId, groupId, t2, t3, 2, 0), // t2 gana
                playedMatch("m5_" + groupId, groupId, t2, t4, 1, 0), // t2 gana
                playedMatch("m6_" + groupId, groupId, t3, t4, 1, 0)  // t3 gana
        ));
    }

    private void mockAllGroups() {
        mockGroup("A", "T_A1", "T_A2", "T_A3", "T_A4");
        mockGroup("B", "T_B1", "T_B2", "T_B3", "T_B4");
        mockGroup("C", "T_C1", "T_C2", "T_C3", "T_C4");
        mockGroup("D", "T_D1", "T_D2", "T_D3", "T_D4");
        mockGroup("E", "T_E1", "T_E2", "T_E3", "T_E4");
        mockGroup("F", "T_F1", "T_F2", "T_F3", "T_F4");
        mockGroup("G", "T_G1", "T_G2", "T_G3", "T_G4");
        mockGroup("H", "T_H1", "T_H2", "T_H3", "T_H4");
        mockGroup("I", "T_I1", "T_I2", "T_I3", "T_I4");
        mockGroup("J", "T_J1", "T_J2", "T_J3", "T_J4");
        mockGroup("K", "T_K1", "T_K2", "T_K3", "T_K4");
        mockGroup("L", "T_L1", "T_L2", "T_L3", "T_L4");
    }


    @Test
    void closeGroupStage_success_returns200() {
        when(knockoutMatchRepository.findAll()).thenReturn(List.of());
        when(knockoutMatchRepository.saveAll(any())).thenReturn(List.of());
        mockAllGroups();

        ResponseEntity<?> response = knockoutController.closeGroupStage();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void closeGroupStage_verifySaveAllWasCalled() {
        when(knockoutMatchRepository.findAll()).thenReturn(List.of());
        when(knockoutMatchRepository.saveAll(any())).thenReturn(List.of());
        mockAllGroups();

        knockoutController.closeGroupStage();

        verify(knockoutMatchRepository).saveAll(any());
    }

    @Test
    void closeGroupStage_generates16Matches() {
        when(knockoutMatchRepository.findAll()).thenReturn(List.of());
        mockAllGroups();

        // Capturamos la lista que se guarda
        when(knockoutMatchRepository.saveAll(any())).thenAnswer(inv -> {
            List<KnockoutMatch> saved = inv.getArgument(0);
            assertEquals(16, saved.size());
            return saved;
        });

        knockoutController.closeGroupStage();
    }

    @Test
    void closeGroupStage_allMatchesAreRoundOf32() {
        when(knockoutMatchRepository.findAll()).thenReturn(List.of());
        mockAllGroups();

        when(knockoutMatchRepository.saveAll(any())).thenAnswer(inv -> {
            List<KnockoutMatch> saved = inv.getArgument(0);
            // todos los partidos deben ser Round of 32
            saved.forEach(m ->
                assertEquals("Round of 32", m.getRound())
            );
            return saved;
        });

        knockoutController.closeGroupStage();
    }

    @Test
    void closeGroupStage_alreadyGenerated_throwsBadRequest() {

    	KnockoutMatch existing = new KnockoutMatch(
                "R32-1", "Round of 32", "T_A1", "T_L2");
        when(knockoutMatchRepository.findAll())
                .thenReturn(List.of(existing));

        assertThrows(BadRequestException.class,
                () -> knockoutController.closeGroupStage());
    }

    @Test
    void closeGroupStage_matchesAreNotPlayed() {
        when(knockoutMatchRepository.findAll()).thenReturn(List.of());
        mockAllGroups();

        when(knockoutMatchRepository.saveAll(any())).thenAnswer(inv -> {
            List<KnockoutMatch> saved = inv.getArgument(0);
            // ningún partido debe estar marcado como jugado
            saved.forEach(m -> assertFalse(m.isPlayed()));
            return saved;
        });

        knockoutController.closeGroupStage();
    }


    @Test
    void getBracket_success_returns200() {
        KnockoutMatch km = new KnockoutMatch(
                "R32-1", "Round of 32", "T_A1", "T_L2");
        when(knockoutMatchRepository.findAll()).thenReturn(List.of(km));
        when(knockoutMatchRepository.findByRound(any())).thenReturn(List.of());

        ResponseEntity<?> response = knockoutController.getBracket();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getBracket_notGenerated_throwsBadRequest() {
        when(knockoutMatchRepository.findAll()).thenReturn(List.of());

        assertThrows(BadRequestException.class,
                () -> knockoutController.getBracket());
    }

    @Test
    void getBracket_containsAllRounds() {
        KnockoutMatch km = new KnockoutMatch(
                "R32-1", "Round of 32", "T_A1", "T_L2");
        when(knockoutMatchRepository.findAll()).thenReturn(List.of(km));
        when(knockoutMatchRepository.findByRound(any())).thenReturn(List.of());

        ResponseEntity<?> response = knockoutController.getBracket();

        @SuppressWarnings("unchecked")
        java.util.Map<String, ?> bracket =
                (java.util.Map<String, ?>) response.getBody();

        assertNotNull(bracket);
        assertTrue(bracket.containsKey("Round of 32"));
        assertTrue(bracket.containsKey("Round of 16"));
        assertTrue(bracket.containsKey("Quarter Finals"));
        assertTrue(bracket.containsKey("Semi Finals"));
        assertTrue(bracket.containsKey("Final"));
    }


    @Test
    void updateKnockoutMatch_homeWins_returns200() {
        KnockoutMatch km = new KnockoutMatch(
                "R32-1", "Round of 32", "COL", "BRA");
        when(knockoutMatchRepository.findById("R32-1"))
                .thenReturn(Optional.of(km));
        when(knockoutMatchRepository.save(any())).thenReturn(km);

        KnockoutMatchDTO dto = new KnockoutMatchDTO(
                "R32-1", "Round of 32", "COL", "BRA",
                2, 1, null, true);

        ResponseEntity<?> response =
                knockoutController.updateKnockoutMatch("R32-1", dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateKnockoutMatch_awayWins_returns200() {
        KnockoutMatch km = new KnockoutMatch(
                "R32-1", "Round of 32", "COL", "BRA");
        when(knockoutMatchRepository.findById("R32-1"))
                .thenReturn(Optional.of(km));
        when(knockoutMatchRepository.save(any())).thenReturn(km);

        KnockoutMatchDTO dto = new KnockoutMatchDTO(
                "R32-1", "Round of 32", "COL", "BRA",
                0, 3, null, true);

        ResponseEntity<?> response =
                knockoutController.updateKnockoutMatch("R32-1", dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateKnockoutMatch_homeWins_winnerIsHome() {
        KnockoutMatch km = new KnockoutMatch(
                "R32-1", "Round of 32", "COL", "BRA");
        when(knockoutMatchRepository.findById("R32-1"))
                .thenReturn(Optional.of(km));
        when(knockoutMatchRepository.save(any())).thenReturn(km);

        KnockoutMatchDTO dto = new KnockoutMatchDTO(
                "R32-1", "Round of 32", "COL", "BRA",
                2, 1, null, true);

        knockoutController.updateKnockoutMatch("R32-1", dto);

        // el ganador debe ser el equipo local
        assertEquals("COL", km.getWinnerId());
    }

    @Test
    void updateKnockoutMatch_awayWins_winnerIsAway() {
        KnockoutMatch km = new KnockoutMatch(
                "R32-1", "Round of 32", "COL", "BRA");
        when(knockoutMatchRepository.findById("R32-1"))
                .thenReturn(Optional.of(km));
        when(knockoutMatchRepository.save(any())).thenReturn(km);

        KnockoutMatchDTO dto = new KnockoutMatchDTO(
                "R32-1", "Round of 32", "COL", "BRA",
                0, 2, null, true);

        knockoutController.updateKnockoutMatch("R32-1", dto);

        assertEquals("BRA", km.getWinnerId());
    }

    @Test
    void updateKnockoutMatch_draw_throwsBadRequest() {
        KnockoutMatch km = new KnockoutMatch(
                "R32-1", "Round of 32", "COL", "BRA");
        when(knockoutMatchRepository.findById("R32-1"))
                .thenReturn(Optional.of(km));

        KnockoutMatchDTO dto = new KnockoutMatchDTO(
                "R32-1", "Round of 32", "COL", "BRA",
                1, 1, null, true);

        assertThrows(BadRequestException.class,
                () -> knockoutController.updateKnockoutMatch("R32-1", dto));
    }

    @Test
    void updateKnockoutMatch_notFound_throwsResourceNotFound() {
        when(knockoutMatchRepository.findById("FAKE"))
                .thenReturn(Optional.empty());

        KnockoutMatchDTO dto = new KnockoutMatchDTO(
                "FAKE", "Round of 32", "COL", "BRA",
                2, 0, null, true);

        assertThrows(ResourceNotFoundException.class,
                () -> knockoutController.updateKnockoutMatch("FAKE", dto));
    }

    @Test
    void updateKnockoutMatch_notPlayed_noWinner() {
        KnockoutMatch km = new KnockoutMatch(
                "R32-1", "Round of 32", "COL", "BRA");
        when(knockoutMatchRepository.findById("R32-1"))
                .thenReturn(Optional.of(km));
        when(knockoutMatchRepository.save(any())).thenReturn(km);

        KnockoutMatchDTO dto = new KnockoutMatchDTO(
                "R32-1", "Round of 32", "COL", "BRA",
                0, 0, null, false);

        knockoutController.updateKnockoutMatch("R32-1", dto);

        assertNull(km.getWinnerId());
    }
}