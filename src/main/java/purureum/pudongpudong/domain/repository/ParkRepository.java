package purureum.pudongpudong.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import purureum.pudongpudong.domain.model.Park;

public interface ParkRepository extends JpaRepository<Park, String> {

}
