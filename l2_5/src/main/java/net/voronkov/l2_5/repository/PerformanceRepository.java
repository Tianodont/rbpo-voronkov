package net.voronkov.l2_5.repository;

import java.util.List;
import net.voronkov.l2_5.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    List<Performance> findByPlayId(Long playId);

    List<Performance> findByHallId(Long hallId);
}
