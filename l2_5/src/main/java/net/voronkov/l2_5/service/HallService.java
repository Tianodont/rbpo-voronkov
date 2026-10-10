package net.voronkov.l2_5.service;

import net.voronkov.l2_5.dto.HallRequest;
import net.voronkov.l2_5.model.Hall;
import net.voronkov.l2_5.model.Performance;
import net.voronkov.l2_5.repository.HallRepository;
import net.voronkov.l2_5.repository.PerformanceRepository;
import net.voronkov.l2_5.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HallService {

    private final HallRepository halls;
    private final PerformanceRepository performances;
    private final TicketRepository tickets;

    public HallService(
            HallRepository halls,
            PerformanceRepository performances,
            TicketRepository tickets) {
        this.halls = halls;
        this.performances = performances;
        this.tickets = tickets;
    }

    public Iterable<Hall> getAll() {
        return halls.findAll();
    }

    public Hall get(Long id) {
        return halls.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found"));
    }

    public Hall create(HallRequest r) {
        Hall h = new Hall();
        h.setName(r.getName() != null ? r.getName().trim() : "");
        h.setCapacity(r.getCapacity() != null ? r.getCapacity() : 0);
        validate(h);

        if (halls.existsByNameIgnoreCase(h.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hall name already exists");
        }

        return halls.save(h);
    }

    public Hall update(Long id, HallRequest r) {
        Hall h = get(id);
        if (r.getName() != null) {
            h.setName(r.getName().trim());
        }
        if (r.getCapacity() != null) {
            h.setCapacity(r.getCapacity());
        }
        validate(h);

        if (halls.existsByNameIgnoreCaseAndIdNot(h.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hall name already exists");
        }

        return halls.save(h);
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        for (Performance p : performances.findByHallId(id)) {
            tickets.deleteAll(tickets.findByPerformanceId(p.getId()));
            performances.delete(p);
        }
        halls.deleteById(id);
    }

    private void validate(Hall h) {
        if (h.getName().isBlank() || h.getCapacity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hall data");
        }
    }
}
