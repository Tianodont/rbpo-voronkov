package net.voronkov.l2.service

import net.voronkov.l2.dto.UserRequest
import net.voronkov.l2.model.User
import net.voronkov.l2.repository.TicketRepository
import net.voronkov.l2.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val users: UserRepository,
    private val tickets: TicketRepository,
) {
    fun getAll() = users.findAll()

    fun get(id: Long) =
        users.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

    fun create(r: UserRequest): User {
        val u =
            User(
                name = r.name?.trim() ?: "",
                age = r.age ?: -1,
            )
        validate(u)
        return users.save(u)
    }

    fun update(
        id: Long,
        r: UserRequest,
    ): User {
        val u = get(id)
        r.name?.let { u.name = it.trim() }
        r.age?.let { u.age = it }
        validate(u)
        return users.save(u)
    }

    @Transactional
    fun delete(id: Long) {
        get(id)
        tickets.deleteAll(tickets.findByUserId(id))
        users.deleteById(id)
    }

    private fun validate(u: User) {
        if (u.name.isBlank() || u.age < 0) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid user data",
            )
        }
    }
}
