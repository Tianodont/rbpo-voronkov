package net.voronkov.l2.repository

import net.voronkov.l2.model.Play
import org.springframework.data.jpa.repository.JpaRepository

interface PlayRepository : JpaRepository<Play, Long>
