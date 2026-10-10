package net.voronkov.l2.controller

import net.voronkov.l2.dto.HallRequest
import net.voronkov.l2.service.HallService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/theatre-halls")
class HallController(
    private val service: HallService,
) {
    @GetMapping
    fun getAll() = service.getAll()

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ) = service.get(id)

    @PostMapping
    fun create(
        @RequestBody r: HallRequest,
    ) = ResponseEntity.status(HttpStatus.CREATED).body(service.create(r))

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody r: HallRequest,
    ) = service.update(id, r)

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
