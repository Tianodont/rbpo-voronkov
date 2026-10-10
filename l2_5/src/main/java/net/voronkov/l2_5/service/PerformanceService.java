package net.voronkov.l2_5.service;

import net.voronkov.l2_5.dto.PerformanceRequest;
import net.voronkov.l2_5.model.Performance;
import net.voronkov.l2_5.model.PerformanceStatus;
import net.voronkov.l2_5.repository.HallRepository;
import net.voronkov.l2_5.repository.PerformanceRepository;
import net.voronkov.l2_5.repository.PlayRepository;
import net.voronkov.l2_5.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PerformanceService {

    private final PerformanceRepository performances;
    private final PlayRepository plays;
    private final HallRepository halls;
    private final TicketRepository tickets;

    public PerformanceService(
            PerformanceRepository performances,
            PlayRepository plays,
            HallRepository halls,
            TicketRepository tickets) {
        this.performances = performances;
        this.plays = plays;
        this.halls = halls;
        this.tickets = tickets;
    }

    public Iterable<Performance> getAll() {
        return performances.findAll();
    }

    public Performance get(Long id) {
        return performances.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found"));
    }

    public Performance create(PerformanceRequest r) {
        Performance p = new Performance();
        p.setPlay(plays.findById(r.getPlayId() != null ? r.getPlayId() : -1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid playId")));
        p.setHall(halls.findById(r.getHallId() != null ? r.getHallId() : -1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hallId")));
        p.setStart(r.getStart());
        p.setPrice(r.getPrice() != null ? r.getPrice() : -1);
        p.setStatus(r.getStatus() != null ? r.getStatus() : PerformanceStatus.SCHEDULED);
        validate(p);
        return performances.save(p);
    }

    public Performance update(Long id, PerformanceRequest r) {
        Performance p = get(id);

        if (r.getPlayId() != null) {
            p.setPlay(plays.findById(r.getPlayId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid playId")));
        }
        if (r.getHallId() != null) {
            p.setHall(halls.findById(r.getHallId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hallId")));
        }
        if (r.getStart() != null) {
            p.setStart(r.getStart());
        }
        if (r.getPrice() != null) {
            p.setPrice(r.getPrice());
        }
        if (r.getStatus() != null) {
            p.setStatus(r.getStatus());
        }

        validate(p);
        return performances.save(p);
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        tickets.deleteAll(tickets.findByPerformanceId(id));
        performances.deleteById(id);
    }

    private void validate(Performance p) {
        if (p.getStart() == null || p.getPrice() < 0 || p.getPlay() == null || p.getHall() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid performance data");
        }
    }
}
