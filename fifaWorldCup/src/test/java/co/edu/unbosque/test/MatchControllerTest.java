package co.edu.unbosque.test;

import co.edu.unbosque.exception.BadRequestException;
import co.edu.unbosque.exception.ResourceNotFoundException;
import co.edu.unbosque.model.Match;
import co.edu.unbosque.model.dto.MatchDTO;
import co.edu.unbosque.persistence.GroupRepository;
import co.edu.unbosque.persistence.MatchRepository;
import co.edu.unbosque.persistence.TeamRepository;
import co.edu.unbosque.controller.MatchController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private MatchController matchController;

    private MatchDTO validDto;
    private Match savedMatch;

    @BeforeEach
    void setUp() {
        validDto = new MatchDTO("M1", "A", "COL", "BRA", 0, 0, false);
        savedMatch = new Match("M1", "A", "COL", "BRA");
    }

    // ── ADD MATCH ─────────────────────────────────────────

    @Test
    void addMatch_success_returns201() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.existsById("COL")).thenReturn(true);
        when(teamRepository.existsById("BRA")).thenReturn(true);
        when(matchRepository.save(any())).thenReturn(savedMatch);

        ResponseEntity<?> response = matchController.addMatch(validDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void addMatch_success_verifySaveWasCalled() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.existsById("COL")).thenReturn(true);
        when(teamRepository.existsById("BRA")).thenReturn(true);
        when(matchRepository.save(any())).thenReturn(savedMatch);

        matchController.addMatch(validDto);

        verify(matchRepository).save(any());
    }

    @Test
    void addMatch_nullDto_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> matchController.addMatch(null));
    }

    @Test
    void addMatch_blankId_throwsBadRequest() {
        MatchDTO bad = new MatchDTO("", "A", "COL", "BRA", 0, 0, false);
        assertThrows(BadRequestException.class,
                () -> matchController.addMatch(bad));
    }

    @Test
    void addMatch_blankGroupId_throwsBadRequest() {
        MatchDTO bad = new MatchDTO("M1", "", "COL", "BRA", 0, 0, false);
        assertThrows(BadRequestException.class,
                () -> matchController.addMatch(bad));
    }

    @Test
    void addMatch_blankHomeTeam_throwsBadRequest() {
        MatchDTO bad = new MatchDTO("M1", "A", "", "BRA", 0, 0, false);
        assertThrows(BadRequestException.class,
                () -> matchController.addMatch(bad));
    }

    @Test
    void addMatch_blankAwayTeam_throwsBadRequest() {
        MatchDTO bad = new MatchDTO("M1", "A", "COL", "", 0, 0, false);
        assertThrows(BadRequestException.class,
                () -> matchController.addMatch(bad));
    }

    @Test
    void addMatch_groupNotFound_throwsBadRequest() {
        when(groupRepository.existsById("A")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> matchController.addMatch(validDto));
    }

    @Test
    void addMatch_homeTeamNotFound_throwsBadRequest() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.existsById("COL")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> matchController.addMatch(validDto));
    }

    @Test
    void addMatch_awayTeamNotFound_throwsBadRequest() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.existsById("COL")).thenReturn(true);
        when(teamRepository.existsById("BRA")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> matchController.addMatch(validDto));
    }

    // ── UPDATE MATCH ──────────────────────────────────────

    @Test
    void updateMatch_success_returns200() {
        when(matchRepository.existsById("M1")).thenReturn(true);
        when(matchRepository.save(any())).thenReturn(savedMatch);

        ResponseEntity<?> response = matchController.updateMatch("M1", validDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateMatch_notFound_throwsResourceNotFound() {
        when(matchRepository.existsById("M1")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> matchController.updateMatch("M1", validDto));
    }

    @Test
    void updateMatch_verifySaveWasCalled() {
        when(matchRepository.existsById("M1")).thenReturn(true);
        when(matchRepository.save(any())).thenReturn(savedMatch);

        matchController.updateMatch("M1", validDto);

        verify(matchRepository).save(any());
    }

    @Test
    void updateMatch_withGoals_returns200() {
        MatchDTO played = new MatchDTO("M1", "A", "COL", "BRA", 2, 1, true);
        when(matchRepository.existsById("M1")).thenReturn(true);
        when(matchRepository.save(any())).thenReturn(savedMatch);

        ResponseEntity<?> response = matchController.updateMatch("M1", played);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ── DELETE MATCH ──────────────────────────────────────

    @Test
    void deleteMatch_success_returns204() {
        when(matchRepository.existsById("M1")).thenReturn(true);
        doNothing().when(matchRepository).deleteById("M1");

        ResponseEntity<?> response = matchController.deleteMatch("M1");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteMatch_notFound_throwsResourceNotFound() {
        when(matchRepository.existsById("XYZ")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> matchController.deleteMatch("XYZ"));
    }

    @Test
    void deleteMatch_verifyDeleteWasCalled() {
        when(matchRepository.existsById("M1")).thenReturn(true);
        doNothing().when(matchRepository).deleteById("M1");

        matchController.deleteMatch("M1");

        verify(matchRepository).deleteById("M1");
    }

    // ── GET MATCHES ───────────────────────────────────────

    @Test
    void getMatchesByTeam_found_returns200() {
        when(teamRepository.existsById("COL")).thenReturn(true);
        when(matchRepository.findByHomeTeamIdOrAwayTeamId("COL", "COL"))
                .thenReturn(List.of(savedMatch));

        ResponseEntity<?> response = matchController.getMatchesByTeam("COL");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getMatchesByTeam_teamNotFound_throwsResourceNotFound() {
        when(teamRepository.existsById("XYZ")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> matchController.getMatchesByTeam("XYZ"));
    }

    @Test
    void getMatchesByTeam_emptyList_returns200() {
        when(teamRepository.existsById("COL")).thenReturn(true);
        when(matchRepository.findByHomeTeamIdOrAwayTeamId("COL", "COL"))
                .thenReturn(List.of());

        ResponseEntity<?> response = matchController.getMatchesByTeam("COL");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getMatchesByGroup_found_returns200() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(matchRepository.findByGroupId("A")).thenReturn(List.of(savedMatch));

        ResponseEntity<?> response = matchController.getMatchesByGroup("A");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getMatchesByGroup_groupNotFound_throwsResourceNotFound() {
        when(groupRepository.existsById("Z")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> matchController.getMatchesByGroup("Z"));
    }

    @Test
    void getMatchesByGroup_emptyList_returns200() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(matchRepository.findByGroupId("A")).thenReturn(List.of());

        ResponseEntity<?> response = matchController.getMatchesByGroup("A");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}