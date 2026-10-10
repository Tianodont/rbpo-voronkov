package net.voronkov.l2_5.repository;

import java.util.List;
import net.voronkov.l2_5.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByPerformanceId(Long performanceId);

    List<Ticket> findByUserId(Long userId);
}
