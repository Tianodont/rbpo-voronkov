package net.voronkov.l2.repository

import net.voronkov.l2.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>
