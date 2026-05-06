package co.edu.unbosque.test;

import co.edu.unbosque.exception.BadRequestException;
import co.edu.unbosque.exception.ResourceNotFoundException;
import co.edu.unbosque.model.Team;
import co.edu.unbosque.model.dto.TeamDTO;
import co.edu.unbosque.persistence.GroupRepository;
import co.edu.unbosque.persistence.TeamRepository;
import co.edu.unbosque.controller.TeamController;
import org.junit.jupiter.api.BeforeEach;
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
class TeamControllerTest {

	@Mock
    private TeamRepository teamRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private TeamController teamController;

    private TeamDTO validDto;
    private Team savedTeam;

    @BeforeEach
    void setUp() {
        validDto = new TeamDTO("COL", "Colombia", "CONMEBOL", "A");
        savedTeam = new Team("COL", "Colombia", "CONMEBOL", "A");
    }

    // ── ADD TEAM ──────────────────────────────────────────

    @Test
    void addTeam_success_returns201() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.count()).thenReturn(10L);
        when(teamRepository.findByGroupId("A")).thenReturn(List.of());
        when(teamRepository.save(any())).thenReturn(savedTeam);

        ResponseEntity<?> response = teamController.addTeam(validDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void addTeam_success_verifySaveWasCalled() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.count()).thenReturn(10L);
        when(teamRepository.findByGroupId("A")).thenReturn(List.of());
        when(teamRepository.save(any())).thenReturn(savedTeam);

        teamController.addTeam(validDto);

        verify(teamRepository).save(any());
    }

    @Test
    void addTeam_nullDto_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> teamController.addTeam(null));
    }

    @Test
    void addTeam_blankId_throwsBadRequest() {
        TeamDTO bad = new TeamDTO("", "Colombia", "CONMEBOL", "A");
        assertThrows(BadRequestException.class,
                () -> teamController.addTeam(bad));
    }

    @Test
    void addTeam_blankName_throwsBadRequest() {
        TeamDTO bad = new TeamDTO("COL", "", "CONMEBOL", "A");
        assertThrows(BadRequestException.class,
                () -> teamController.addTeam(bad));
    }

    @Test
    void addTeam_groupNotFound_throwsBadRequest() {
        when(groupRepository.existsById("A")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> teamController.addTeam(validDto));
    }

    @Test
    void addTeam_maxTotalTeamsReached_throwsBadRequest() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.count()).thenReturn(48L);

        assertThrows(BadRequestException.class,
                () -> teamController.addTeam(validDto));
    }

    @Test
    void addTeam_groupFull_throwsBadRequest() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.count()).thenReturn(10L);
        when(teamRepository.findByGroupId("A")).thenReturn(List.of(
                new Team("T1", "Team1", "UEFA",     "A"),
                new Team("T2", "Team2", "AFC",      "A"),
                new Team("T3", "Team3", "CAF",      "A"),
                new Team("T4", "Team4", "CONCACAF", "A")
        ));

        assertThrows(BadRequestException.class,
                () -> teamController.addTeam(validDto));
    }

    @Test
    void addTeam_duplicateConmebol_throwsBadRequest() {
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.count()).thenReturn(10L);
        when(teamRepository.findByGroupId("A")).thenReturn(List.of(
                new Team("ECU", "Ecuador", "CONMEBOL", "A")
        ));

        assertThrows(BadRequestException.class,
                () -> teamController.addTeam(validDto));
    }

    @Test
    void addTeam_uefaFirst_success() {
        TeamDTO uefaDto = new TeamDTO("ESP", "España", "UEFA", "A");
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.count()).thenReturn(10L);
        when(teamRepository.findByGroupId("A")).thenReturn(List.of());
        when(teamRepository.save(any()))
                .thenReturn(new Team("ESP", "España", "UEFA", "A"));

        ResponseEntity<?> response = teamController.addTeam(uefaDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void addTeam_uefaSecond_success() {
        TeamDTO uefaDto = new TeamDTO("GER", "Alemania", "UEFA", "A");
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.count()).thenReturn(10L);
        when(teamRepository.findByGroupId("A")).thenReturn(List.of(
                new Team("ESP", "España", "UEFA", "A")
        ));
        when(teamRepository.save(any()))
                .thenReturn(new Team("GER", "Alemania", "UEFA", "A"));

        ResponseEntity<?> response = teamController.addTeam(uefaDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void addTeam_uefaThird_throwsBadRequest() {
        TeamDTO uefaDto = new TeamDTO("ITA", "Italia", "UEFA", "A");
        when(groupRepository.existsById("A")).thenReturn(true);
        when(teamRepository.count()).thenReturn(10L);
        when(teamRepository.findByGroupId("A")).thenReturn(List.of(
                new Team("ESP", "España",  "UEFA", "A"),
                new Team("GER", "Alemania","UEFA", "A")
        ));

        assertThrows(BadRequestException.class,
                () -> teamController.addTeam(uefaDto));
    }

    // ── UPDATE TEAM ───────────────────────────────────────

    @Test
    void updateTeam_success_returns200() {
        when(teamRepository.existsById("COL")).thenReturn(true);
        when(teamRepository.save(any())).thenReturn(savedTeam);

        ResponseEntity<?> response = teamController.updateTeam("COL", validDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateTeam_notFound_throwsResourceNotFound() {
        when(teamRepository.existsById("COL")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> teamController.updateTeam("COL", validDto));
    }

    @Test
    void updateTeam_verifySaveWasCalled() {
        when(teamRepository.existsById("COL")).thenReturn(true);
        when(teamRepository.save(any())).thenReturn(savedTeam);

        teamController.updateTeam("COL", validDto);

        verify(teamRepository).save(any());
    }

    // ── DELETE TEAM ───────────────────────────────────────

    @Test
    void deleteTeam_success_returns204() {
        when(teamRepository.existsById("COL")).thenReturn(true);
        doNothing().when(teamRepository).deleteById("COL");

        ResponseEntity<?> response = teamController.deleteTeam("COL");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteTeam_notFound_throwsResourceNotFound() {
        when(teamRepository.existsById("XYZ")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> teamController.deleteTeam("XYZ"));
    }

    @Test
    void deleteTeam_verifyDeleteWasCalled() {
        when(teamRepository.existsById("COL")).thenReturn(true);
        doNothing().when(teamRepository).deleteById("COL");

        teamController.deleteTeam("COL");

        verify(teamRepository).deleteById("COL");
    }

    // ── GET TEAMS ─────────────────────────────────────────

    @Test
    void getAllTeams_returns200() {
        when(teamRepository.findAll()).thenReturn(List.of(savedTeam));

        ResponseEntity<?> response = teamController.getAllTeams();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllTeams_emptyList_returns200() {
        when(teamRepository.findAll()).thenReturn(List.of());

        ResponseEntity<?> response = teamController.getAllTeams();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTeamById_found_returns200() {
        when(teamRepository.findById("COL")).thenReturn(Optional.of(savedTeam));

        ResponseEntity<?> response = teamController.getTeamById("COL");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTeamById_notFound_throwsResourceNotFound() {
        when(teamRepository.findById("XYZ")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> teamController.getTeamById("XYZ"));
    }
}