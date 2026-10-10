package net.voronkov.l2_5.controller;

import net.voronkov.l2_5.dto.PlayRequest;
import net.voronkov.l2_5.service.PlayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plays")
public class PlayController {

    private final PlayService service;

    public PlayController(PlayService service) {
        this.service = service;
    }

    @GetMapping
    public Iterable<?> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PlayRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @PatchMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody PlayRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
