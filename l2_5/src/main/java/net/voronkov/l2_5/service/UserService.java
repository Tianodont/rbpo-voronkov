package net.voronkov.l2_5.service;

import net.voronkov.l2_5.dto.UserRequest;
import net.voronkov.l2_5.model.User;
import net.voronkov.l2_5.repository.TicketRepository;
import net.voronkov.l2_5.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository users;
    private final TicketRepository tickets;

    public UserService(UserRepository users, TicketRepository tickets) {
        this.users = users;
        this.tickets = tickets;
    }

    public Iterable<User> getAll() {
        return users.findAll();
    }

    public User get(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User create(UserRequest r) {
        User u = new User();
        u.setName(r.getName() != null ? r.getName().trim() : "");
        u.setAge(r.getAge() != null ? r.getAge() : -1);
        validate(u);
        return users.save(u);
    }

    public User update(Long id, UserRequest r) {
        User u = get(id);
        if (r.getName() != null) {
            u.setName(r.getName().trim());
        }
        if (r.getAge() != null) {
            u.setAge(r.getAge());
        }
        validate(u);
        return users.save(u);
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        tickets.deleteAll(tickets.findByUserId(id));
        users.deleteById(id);
    }

    private void validate(User u) {
        if (u.getName().isBlank() || u.getAge() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user data");
        }
    }
}
