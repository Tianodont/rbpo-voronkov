package net.voronkov.l2.service

import net.voronkov.l2.dto.PerformanceRequest
import net.voronkov.l2.model.Performance
import net.voronkov.l2.model.PerformanceStatus
import net.voronkov.l2.repository.HallRepository
import net.voronkov.l2.repository.PerformanceRepository
import net.voronkov.l2.repository.PlayRepository
import net.voronkov.l2.repository.TicketRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PerformanceService(
    private val performances: PerformanceRepository,
    private val plays: PlayRepository,
    private val halls: HallRepository,
    private val tickets: TicketRepository,
) {
    fun getAll() = performances.findAll()

    fun get(id: Long) =
        performances.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found")
        }

    fun create(r: PerformanceRequest): Performance {
        val p =
            Performance(
                play =
                    plays.findById(r.playId ?: -1).orElseThrow {
                        ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid playId")
                    },
                hall =
                    halls.findById(r.hallId ?: -1).orElseThrow {
                        ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hallId")
                    },
                start = r.start,
                price = r.price ?: -1,
                status = r.status ?: PerformanceStatus.SCHEDULED,
            )
        validate(p)
        return performances.save(p)
    }

    fun update(
        id: Long,
        r: PerformanceRequest,
    ): Performance {
        val p = get(id)

        r.playId?.let {
            p.play =
                plays.findById(it).orElseThrow {
                    ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid playId")
                }
        }
        r.hallId?.let {
            p.hall =
                halls.findById(it).orElseThrow {
                    ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hallId")
                }
        }
        r.start?.let { p.start = it }
        r.price?.let { p.price = it }
        r.status?.let { p.status = it }

        validate(p)
        return performances.save(p)
    }

    @Transactional
    fun delete(id: Long) {
        get(id)
        tickets.deleteAll(tickets.findByPerformanceId(id))
        performances.deleteById(id)
    }

    private fun validate(p: Performance) {
        if (p.start == null || p.price < 0 || p.play == null || p.hall == null) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid performance data",
            )
        }
    }
}
