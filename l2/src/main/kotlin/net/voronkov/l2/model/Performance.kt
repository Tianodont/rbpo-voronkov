package net.voronkov.l2.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "performances")
class Performance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(optional = false)
    @JoinColumn(name = "play_id", nullable = false)
    var play: Play? = null,
    @ManyToOne(optional = false)
    @JoinColumn(name = "hall_id", nullable = false)
    var hall: Hall? = null,
    @Column(nullable = false)
    var start: LocalDateTime? = null,
    @Column(nullable = false)
    var price: Int = 0,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PerformanceStatus = PerformanceStatus.SCHEDULED,
)
