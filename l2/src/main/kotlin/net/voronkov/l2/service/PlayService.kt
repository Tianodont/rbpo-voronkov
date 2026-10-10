package net.voronkov.l2.service

import net.voronkov.l2.dto.PlayRequest
import net.voronkov.l2.model.Play
import net.voronkov.l2.repository.PerformanceRepository
import net.voronkov.l2.repository.PlayRepository
import net.voronkov.l2.repository.TicketRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PlayService(
    private val plays: PlayRepository,
    private val performances: PerformanceRepository,
    private val tickets: TicketRepository,
) {
    fun getAll() = plays.findAll()

    fun get(id: Long) =
        plays.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Play not found")
        }

    fun create(r: PlayRequest): Play {
        val p =
            Play(
                name = r.name?.trim() ?: "",
                duration = r.duration ?: 0,
                minAge = r.minAge ?: -1,
            )
        validate(p)
        return plays.save(p)
    }

    fun update(
        id: Long,
        r: PlayRequest,
    ): Play {
        val p = get(id)
        r.name?.let { p.name = it.trim() }
        r.duration?.let { p.duration = it }
        r.minAge?.let { p.minAge = it }
        validate(p)
        return plays.save(p)
    }

    @Transactional
    fun delete(id: Long) {
        get(id)
        performances.findByPlayId(id).forEach { performance ->
            tickets.deleteAll(tickets.findByPerformanceId(performance.id!!))
            performances.delete(performance)
        }
        plays.deleteById(id)
    }

    private fun validate(p: Play) {
        if (p.name.isBlank() || p.duration <= 0 || p.minAge < 0) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid play data",
            )
        }
    }
}
