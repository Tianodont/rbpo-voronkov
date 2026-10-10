package net.voronkov.l2.repository

import net.voronkov.l2.model.Performance
import org.springframework.data.jpa.repository.JpaRepository

interface PerformanceRepository : JpaRepository<Performance, Long> {
    fun findByPlayId(playId: Long): List<Performance>

    fun findByHallId(hallId: Long): List<Performance>
}
