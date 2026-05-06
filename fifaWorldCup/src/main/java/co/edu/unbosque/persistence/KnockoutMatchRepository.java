package co.edu.unbosque.persistence;

import co.edu.unbosque.model.KnockoutMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnockoutMatchRepository extends JpaRepository<KnockoutMatch, String> {
	List<KnockoutMatch> findByRound(String round);
}