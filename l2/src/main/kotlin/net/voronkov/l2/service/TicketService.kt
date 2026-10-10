package net.voronkov.l2.service

import net.voronkov.l2.dto.TicketRequest
import net.voronkov.l2.model.PerformanceStatus
import net.voronkov.l2.model.Ticket
import net.voronkov.l2.model.TicketStatus
import net.voronkov.l2.repository.PerformanceRepository
import net.voronkov.l2.repository.TicketRepository
import net.voronkov.l2.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TicketService(
    private val tickets: TicketRepository,
    private val performances: PerformanceRepository,
    private val users: UserRepository,
) {
    fun getAll() = tickets.findAll()

    fun get(id: Long) =
        tickets.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found")
        }

    fun create(r: TicketRequest): Ticket {
        val t =
            Ticket(
                performance =
                    performances.findById(r.performanceId ?: -1).orElseThrow {
                        ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid performanceId")
                    },
                user =
                    users.findById(r.userId ?: -1).orElseThrow {
                        ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId")
                    },
                place = r.place ?: 0,
                status = r.status ?: TicketStatus.RESERVED,
            )
        validate(t)
        return tickets.save(t)
    }

    fun update(
        id: Long,
        r: TicketRequest,
    ): Ticket {
        val t = get(id)

        r.performanceId?.let {
            t.performance =
                performances.findById(it).orElseThrow {
                    ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid performanceId")
                }
        }
        r.userId?.let {
            t.user =
                users.findById(it).orElseThrow {
                    ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId")
                }
        }
        r.place?.let { t.place = it }
        r.status?.let { t.status = it }

        validate(t, id)
        return tickets.save(t)
    }

    fun delete(id: Long) {
        get(id)
        tickets.deleteById(id)
    }

    private fun validate(
        t: Ticket,
        currentId: Long? = null,
    ) {
        val performance =
            t.performance
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid performance")
        val hall =
            performance.hall
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hall")
        val user =
            t.user
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user")
        val play =
            performance.play
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid play")

        if (t.place <= 0 || t.place > hall.capacity) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ticket data")
        }

        if (performance.status != PerformanceStatus.SCHEDULED &&
            t.status != TicketStatus.CANCELLED
        ) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Performance is not available",
            )
        }

        if (t.status == TicketStatus.CANCELLED) {
            return
        }

        if (user.age < play.minAge) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "User does not meet minimum age",
            )
        }

        val placeTaken =
            tickets.findByPerformanceId(performance.id!!).any {
                it.id != currentId &&
                    it.place == t.place &&
                    it.status != TicketStatus.CANCELLED
            }
        if (placeTaken) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Place is already occupied")
        }
    }
}
