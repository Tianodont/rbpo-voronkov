package net.voronkov.l2.repository

import net.voronkov.l2.model.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<Ticket, Long> {
    fun findByPerformanceId(performanceId: Long): List<Ticket>

    fun findByUserId(userId: Long): List<Ticket>
}
