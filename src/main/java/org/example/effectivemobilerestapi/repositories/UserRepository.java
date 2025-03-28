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
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.createdTasks LEFT JOIN FETCH u.assignedTasks WHERE u.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);
}
