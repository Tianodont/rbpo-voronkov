package net.voronkov.l2.dto

import net.voronkov.l2.model.PerformanceStatus
import net.voronkov.l2.model.TicketStatus
import java.time.LocalDateTime

data class PlayRequest(
    val name: String? = null,
    val duration: Int? = null,
    val minAge: Int? = null,
)

data class HallRequest(
    val name: String? = null,
    val capacity: Int? = null,
)

data class PerformanceRequest(
    val playId: Long? = null,
    val hallId: Long? = null,
    val start: LocalDateTime? = null,
    val price: Int? = null,
    val status: PerformanceStatus? = null,
)

data class TicketRequest(
    val performanceId: Long? = null,
    val userId: Long? = null,
    val place: Int? = null,
    val status: TicketStatus? = null,
)

data class UserRequest(
    val name: String? = null,
    val age: Int? = null,
)
