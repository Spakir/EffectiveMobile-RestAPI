package org.example.effectivemobilerestapi.repositories;

import org.example.effectivemobilerestapi.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"createdTasks", "assignedTasks"})
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
