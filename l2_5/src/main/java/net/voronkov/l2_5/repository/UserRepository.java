package net.voronkov.l2_5.repository;

import net.voronkov.l2_5.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
