package net.voronkov.l2.service

import net.voronkov.l2.dto.HallRequest
import net.voronkov.l2.model.Hall
import net.voronkov.l2.repository.HallRepository
import net.voronkov.l2.repository.PerformanceRepository
import net.voronkov.l2.repository.TicketRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class HallService(
    private val halls: HallRepository,
    private val performances: PerformanceRepository,
    private val tickets: TicketRepository,
) {
    fun getAll() = halls.findAll()

    fun get(id: Long) =
        halls.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found")
        }

    fun create(r: HallRequest): Hall {
        val h =
            Hall(
                name = r.name?.trim() ?: "",
                capacity = r.capacity ?: 0,
            )
        validate(h)

        if (halls.existsByNameIgnoreCase(h.name)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Hall name already exists",
            )
        }

        return halls.save(h)
    }

    fun update(
        id: Long,
        r: HallRequest,
    ): Hall {
        val h = get(id)
        r.name?.let { h.name = it.trim() }
        r.capacity?.let { h.capacity = it }
        validate(h)

        if (halls.existsByNameIgnoreCaseAndIdNot(h.name, id)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Hall name already exists",
            )
        }

        return halls.save(h)
    }

    @Transactional
    fun delete(id: Long) {
        get(id)
        performances.findByHallId(id).forEach { p ->
            tickets.deleteAll(tickets.findByPerformanceId(p.id!!))
            performances.delete(p)
        }
        halls.deleteById(id)
    }

    private fun validate(h: Hall) {
        if (h.name.isBlank() || h.capacity <= 0) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid hall data",
            )
        }
    }
}
