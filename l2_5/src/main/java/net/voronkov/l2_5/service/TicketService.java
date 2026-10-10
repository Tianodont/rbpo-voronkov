package net.voronkov.l2_5.service;

import net.voronkov.l2_5.dto.TicketRequest;
import net.voronkov.l2_5.model.Hall;
import net.voronkov.l2_5.model.Performance;
import net.voronkov.l2_5.model.PerformanceStatus;
import net.voronkov.l2_5.model.Play;
import net.voronkov.l2_5.model.Ticket;
import net.voronkov.l2_5.model.TicketStatus;
import net.voronkov.l2_5.model.User;
import net.voronkov.l2_5.repository.PerformanceRepository;
import net.voronkov.l2_5.repository.TicketRepository;
import net.voronkov.l2_5.repository.UserRepository;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TicketService {

    private final TicketRepository tickets;
    private final PerformanceRepository performances;
    private final UserRepository users;

    public TicketService(
            TicketRepository tickets, PerformanceRepository performances, UserRepository users) {
        this.tickets = tickets;
        this.performances = performances;
        this.users = users;
    }

    public Iterable<Ticket> getAll() {
        return tickets.findAll();
    }

    public Ticket get(Long id) {
        return tickets.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    public Ticket create(TicketRequest r) {
        Ticket t = new Ticket();
        t.setPerformance(performances.findById(r.getPerformanceId() != null ? r.getPerformanceId() : -1L)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid performanceId")));
        t.setUser(users.findById(r.getUserId() != null ? r.getUserId() : -1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId")));
        t.setPlace(r.getPlace() != null ? r.getPlace() : 0);
        t.setStatus(r.getStatus() != null ? r.getStatus() : TicketStatus.RESERVED);
        validate(t, null);
        return tickets.save(t);
    }

    public Ticket update(Long id, TicketRequest r) {
        Ticket t = get(id);

        if (r.getPerformanceId() != null) {
            t.setPerformance(performances.findById(r.getPerformanceId())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid performanceId")));
        }
        if (r.getUserId() != null) {
            t.setUser(users.findById(r.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId")));
        }
        if (r.getPlace() != null) {
            t.setPlace(r.getPlace());
        }
        if (r.getStatus() != null) {
            t.setStatus(r.getStatus());
        }

        validate(t, id);
        return tickets.save(t);
    }

    public void delete(Long id) {
        get(id);
        tickets.deleteById(id);
    }

    private void validate(Ticket t, Long currentId) {
        Performance performance = t.getPerformance();
        if (performance == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid performance");
        }
        Hall hall = performance.getHall();
        if (hall == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hall");
        }
        User user = t.getUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user");
        }
        Play play = performance.getPlay();
        if (play == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid play");
        }

        if (t.getPlace() <= 0 || t.getPlace() > hall.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ticket data");
        }

        if (performance.getStatus() != PerformanceStatus.SCHEDULED && t.getStatus() != TicketStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Performance is not available");
        }

        if (t.getStatus() == TicketStatus.CANCELLED) {
            return;
        }

        if (user.getAge() < play.getMinAge()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not meet minimum age");
        }

        Long performanceId = performance.getId();
        boolean placeTaken = tickets.findByPerformanceId(performanceId).stream()
                .anyMatch(it -> !Objects.equals(it.getId(), currentId)
                        && it.getPlace() == t.getPlace()
                        && it.getStatus() != TicketStatus.CANCELLED);
        if (placeTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Place is already occupied");
        }
    }
}
