package net.voronkov.l2.repository

import net.voronkov.l2.model.Hall
import org.springframework.data.jpa.repository.JpaRepository

interface HallRepository : JpaRepository<Hall, Long> {
    fun existsByNameIgnoreCase(name: String): Boolean

    fun existsByNameIgnoreCaseAndIdNot(
        name: String,
        id: Long,
    ): Boolean
}
