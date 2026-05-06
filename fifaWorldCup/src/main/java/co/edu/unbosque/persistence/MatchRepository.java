package co.edu.unbosque.persistence;

import co.edu.unbosque.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, String> {
	List<Match> findByGroupId(String groupId);

	List<Match> findByHomeTeamIdOrAwayTeamId(String homeTeamId, String awayTeamId);
}	