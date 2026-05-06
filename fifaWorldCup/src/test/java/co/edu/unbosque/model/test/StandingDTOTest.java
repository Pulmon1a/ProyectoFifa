package co.edu.unbosque.model.test;

import co.edu.unbosque.model.dto.StandingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StandingDTOTest {

    private StandingDTO standing;

    @BeforeEach
    void setUp() {
        standing = new StandingDTO("COL", "Colombia");
    }

    @Test
    void testWin_pointsIncrease() {
        standing.addMatch(3, 1, 0, 0);
        assertEquals(3, standing.getPoints());
    }

    @Test
    void testWin_wonCountIncrease() {
        standing.addMatch(3, 1, 0, 0);
        assertEquals(1, standing.getWon());
    }

    @Test
    void testWin_goalDifference() {
        standing.addMatch(3, 1, 0, 0);
        assertEquals(2, standing.getGoalDifference());
    }

    @Test
    void testWin_playedIncrease() {
        standing.addMatch(3, 1, 0, 0);
        assertEquals(1, standing.getPlayed());
    }

    @Test
    void testDraw_pointsIncrease() {
        standing.addMatch(1, 1, 0, 0);
        assertEquals(1, standing.getPoints());
    }

    @Test
    void testDraw_drawnCountIncrease() {
        standing.addMatch(1, 1, 0, 0);
        assertEquals(1, standing.getDrawn());
    }

    @Test
    void testDraw_goalDifferenceIsZero() {
        standing.addMatch(2, 2, 0, 0);
        assertEquals(0, standing.getGoalDifference());
    }

    @Test
    void testLoss_noPoints() {
        standing.addMatch(0, 2, 0, 0);
        assertEquals(0, standing.getPoints());
    }

    @Test
    void testLoss_lostCountIncrease() {
        standing.addMatch(0, 2, 0, 0);
        assertEquals(1, standing.getLost());
    }

    @Test
    void testLoss_negativeGoalDifference() {
        standing.addMatch(0, 3, 0, 0);
        assertEquals(-3, standing.getGoalDifference());
    }

    @Test
    void testMultipleMatches_totalPoints() {
        standing.addMatch(2, 0, 0, 0);
        standing.addMatch(1, 1, 0, 0);
        standing.addMatch(0, 1, 0, 0);
        assertEquals(4, standing.getPoints());
    }

    @Test
    void testMultipleMatches_playedCount() {
        standing.addMatch(2, 0, 0, 0);
        standing.addMatch(1, 1, 0, 0);
        standing.addMatch(0, 1, 0, 0);
        assertEquals(3, standing.getPlayed());
    }

    @Test
    void testMultipleMatches_goalsFor() {
        standing.addMatch(2, 0, 0, 0);
        standing.addMatch(1, 1, 0, 0);
        standing.addMatch(0, 1, 0, 0);
        assertEquals(3, standing.getGoalsFor());
    }

    @Test
    void testMultipleMatches_goalsAgainst() {
        standing.addMatch(2, 0, 0, 0);
        standing.addMatch(1, 1, 0, 0);
        standing.addMatch(0, 1, 0, 0);
        assertEquals(2, standing.getGoalsAgainst());
    }

    @Test
    void testMultipleMatches_goalDifference() {
        standing.addMatch(2, 0, 0, 0);
        standing.addMatch(1, 1, 0, 0);
        standing.addMatch(0, 1, 0, 0);
        assertEquals(1, standing.getGoalDifference());
    }

    @Test
    void testInitialState_allZero() {
        assertEquals(0, standing.getPoints());
        assertEquals(0, standing.getPlayed());
        assertEquals(0, standing.getWon());
        assertEquals(0, standing.getDrawn());
        assertEquals(0, standing.getLost());
        assertEquals(0, standing.getGoalsFor());
        assertEquals(0, standing.getGoalsAgainst());
        assertEquals(0, standing.getGoalDifference());
        assertEquals(0, standing.getYellowCards());
        assertEquals(0, standing.getRedCards());
    }

    @Test
    void testInitialState_teamInfo() {
        assertEquals("COL", standing.getTeamId());
        assertEquals("Colombia", standing.getTeamName());
    }

    @Test
    void testBigWin_goalDifference() {
        standing.addMatch(7, 0, 0, 0);
        assertEquals(7, standing.getGoalDifference());
        assertEquals(3, standing.getPoints());
    }

    @Test
    void testZeroZero_draw() {
        standing.addMatch(0, 0, 0, 0);
        assertEquals(1, standing.getPoints());
        assertEquals(1, standing.getDrawn());
        assertEquals(0, standing.getGoalDifference());
    }

    @Test
    void testYellowCards_accumulate() {
        standing.addMatch(2, 0, 3, 0);
        standing.addMatch(1, 1, 2, 0);
        assertEquals(5, standing.getYellowCards());
    }

    @Test
    void testRedCards_accumulate() {
        standing.addMatch(1, 0, 0, 1);
        standing.addMatch(0, 1, 0, 2);
        assertEquals(3, standing.getRedCards());
    }

    @Test
    void testCards_initialZero() {
        assertEquals(0, standing.getYellowCards());
        assertEquals(0, standing.getRedCards());
    }
}