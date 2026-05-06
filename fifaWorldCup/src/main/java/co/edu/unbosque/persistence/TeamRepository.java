package co.edu.unbosque.persistence;

import co.edu.unbosque.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
	List<Team> findByGroupId(String groupId);

	List<Team> findByConfederation(String confederation);
}