package purureum.pudongpudong.domain.repository;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import purureum.pudongpudong.domain.model.Park;

import java.util.List;


public interface ParkRepository extends JpaRepository<Park, String> {
	@EntityGraph(attributePaths = {"fairy", "reviews"})
	List<Park> findParksByPlaceNameContaining(String keyword);
}
