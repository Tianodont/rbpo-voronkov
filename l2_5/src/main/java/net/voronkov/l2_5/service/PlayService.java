package net.voronkov.l2_5.service;

import net.voronkov.l2_5.dto.PlayRequest;
import net.voronkov.l2_5.model.Performance;
import net.voronkov.l2_5.model.Play;
import net.voronkov.l2_5.repository.PerformanceRepository;
import net.voronkov.l2_5.repository.PlayRepository;
import net.voronkov.l2_5.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlayService {

    private final PlayRepository plays;
    private final PerformanceRepository performances;
    private final TicketRepository tickets;

    public PlayService(
            PlayRepository plays,
            PerformanceRepository performances,
            TicketRepository tickets) {
        this.plays = plays;
        this.performances = performances;
        this.tickets = tickets;
    }

    public Iterable<Play> getAll() {
        return plays.findAll();
    }

    public Play get(Long id) {
        return plays.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Play not found"));
    }

    public Play create(PlayRequest r) {
        Play p = new Play();
        p.setName(r.getName() != null ? r.getName().trim() : "");
        p.setDuration(r.getDuration() != null ? r.getDuration() : 0);
        p.setMinAge(r.getMinAge() != null ? r.getMinAge() : -1);
        validate(p);
        return plays.save(p);
    }

    public Play update(Long id, PlayRequest r) {
        Play p = get(id);
        if (r.getName() != null) {
            p.setName(r.getName().trim());
        }
        if (r.getDuration() != null) {
            p.setDuration(r.getDuration());
        }
        if (r.getMinAge() != null) {
            p.setMinAge(r.getMinAge());
        }
        validate(p);
        return plays.save(p);
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        for (Performance performance : performances.findByPlayId(id)) {
            tickets.deleteAll(tickets.findByPerformanceId(performance.getId()));
            performances.delete(performance);
        }
        plays.deleteById(id);
    }

    private void validate(Play p) {
        if (p.getName().isBlank() || p.getDuration() <= 0 || p.getMinAge() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid play data");
        }
    }
}
