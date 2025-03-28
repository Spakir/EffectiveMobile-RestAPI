package org.example.effectivemobilerestapi.repositories;

import org.example.effectivemobilerestapi.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"comments", "author", "executor"})
    Page<Task> findByAuthorEmail(String author, Pageable pageable);

    @EntityGraph(attributePaths = {"comments", "author", "executor"})
    Page<Task> findByExecutorEmail(String executor, Pageable pageable);

    @EntityGraph(attributePaths = {"comments", "author", "executor"})
    Optional<Task> findById(@Param("id") Long id);
}
