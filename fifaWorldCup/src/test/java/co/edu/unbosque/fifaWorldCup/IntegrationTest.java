package co.edu.unbosque.fifaWorldCup;

import co.edu.unbosque.model.Group;
import co.edu.unbosque.model.dto.MatchDTO;
import co.edu.unbosque.model.dto.TeamDTO;
import co.edu.unbosque.persistence.GroupRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = "spring.sql.init.mode=never")
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GroupRepository groupRepository;

    @BeforeEach
    void insertGroups() {
        groupRepository.save(new Group("A"));
        groupRepository.save(new Group("B"));
        groupRepository.save(new Group("C"));
        groupRepository.save(new Group("D"));
        groupRepository.save(new Group("E"));
        groupRepository.save(new Group("F"));
        groupRepository.save(new Group("G"));
        groupRepository.save(new Group("H"));
        groupRepository.save(new Group("I"));
        groupRepository.save(new Group("J"));
        groupRepository.save(new Group("K"));
        groupRepository.save(new Group("L"));
    }


    @Test
    @WithMockUser(roles = "FIFA")
    void getTeams_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/teams"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "FIFA")
    void addTeam_validTeam_returns201() throws Exception {
        TeamDTO dto = new TeamDTO("COL", "Colombia", "CONMEBOL", "A");

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("COL"))
                .andExpect(jsonPath("$.name").value("Colombia"));
    }

    @Test
    @WithMockUser(roles = "FIFA")
    void addTeam_groupNotFound_returns400() throws Exception {
        TeamDTO dto = new TeamDTO("COL", "Colombia", "CONMEBOL", "Z");

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "FIFA")
    void addTeam_confederacionDuplicada_returns400() throws Exception {

    	TeamDTO dto1 = new TeamDTO("COL", "Colombia", "CONMEBOL", "A");
        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        TeamDTO dto2 = new TeamDTO("ARG", "Argentina", "CONMEBOL", "A");
        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(roles = "FIFA")
    void deleteTeam_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/teams/FAKE"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "FIFA")
    void addAndDeleteTeam_returns204() throws Exception {
        TeamDTO dto = new TeamDTO("ARG", "Argentina", "CONMEBOL", "B");

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/teams/ARG"))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(roles = "FIFA")
    void addMatch_validMatch_returns201() throws Exception {
        TeamDTO home = new TeamDTO("COL", "Colombia", "CONMEBOL", "A");
        TeamDTO away = new TeamDTO("GER", "Alemania", "UEFA", "A");

        mockMvc.perform(post("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(home)));

        mockMvc.perform(post("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(away)));

        MatchDTO dto = new MatchDTO("M1", "A", "COL", "GER", 0, 0, false);

        mockMvc.perform(post("/api/v1/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("M1"));
    }

    @Test
    @WithMockUser(roles = "FIFA")
    void addMatch_teamNotFound_returns400() throws Exception {
        MatchDTO dto = new MatchDTO("M1", "A", "FAKE1", "FAKE2", 0, 0, false);

        mockMvc.perform(post("/api/v1/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "FIFA")
    void deleteMatch_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/matches/FAKE"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "FAN")
    void getAllGroups_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/groups"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "FAN")
    void getGroupById_validGroup_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/groups/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("A"));
    }

    @Test
    @WithMockUser(roles = "FAN")
    void getGroupById_invalidGroup_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/groups/Z"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "FAN")
    void getGroupStandings_validGroup_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/groups/A/standings"))
                .andExpect(status().isOk());
    }


    @Test
    void addTeam_withoutAuth_returns401() throws Exception {
        TeamDTO dto = new TeamDTO("COL", "Colombia", "CONMEBOL", "A");

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "FAN")
    void addTeam_withFanRole_returns403() throws Exception {
        TeamDTO dto = new TeamDTO("COL", "Colombia", "CONMEBOL", "A");

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FAN")
    void deleteTeam_withFanRole_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/teams/COL"))
                .andExpect(status().isForbidden());
    }
}